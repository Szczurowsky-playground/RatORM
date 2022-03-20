package pl.szczurowsky.ratorm.database.implementation;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import pl.szczurowsky.ratorm.annotation.Model;
import pl.szczurowsky.ratorm.annotation.ModelField;
import pl.szczurowsky.ratorm.database.Database;
import pl.szczurowsky.ratorm.enums.FilterExpression;
import pl.szczurowsky.ratorm.exception.*;
import pl.szczurowsky.ratorm.serializers.BigIntSerializer;
import pl.szczurowsky.ratorm.serializers.MapSerializer;
import pl.szczurowsky.ratorm.serializers.Serializer;
import pl.szczurowsky.ratorm.serializers.UuidSerializer;
import pl.szczurowsky.ratorm.serializers.basic.*;
import pl.szczurowsky.ratorm.serializers.primitive.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MongoDB implements Database {

    private MongoClient client;
    private MongoDatabase database;
    private final HashMap<Object, Class<?>> objects = new HashMap<>();
    private final HashMap<Class<?>, Class<? extends Serializer>> serializers = new HashMap<>();
    private final HashMap<Class<?>, MongoCollection<Document>> collections = new HashMap<>();
    private boolean connected;

    /**
     * Register default serializers
     */
    public MongoDB() {
        this.serializers.put(String.class, StringSerializer.class);
        this.serializers.put(Character.class, CharacterSerializer.class);
        this.serializers.put(char.class, CharSerializer.class);
        this.serializers.put(Integer.class, IntegerSerializer.class);
        this.serializers.put(int.class, IntSerializer.class);
        this.serializers.put(Long.class, LongSerializer.class);
        this.serializers.put(long.class, LongPrimSerializer.class);
        this.serializers.put(BigInteger.class, BigIntSerializer.class);
        this.serializers.put(Float.class, FloatSerializer.class);
        this.serializers.put(float.class, FloatPrimSerializer.class);
        this.serializers.put(Boolean.class, BooleanSerializer.class);
        this.serializers.put(boolean.class, BoolSerializer.class);
        this.serializers.put(Double.class, DoubleSerializer.class);
        this.serializers.put(double.class, DoublePrimSerializer.class);
        this.serializers.put(Short.class, ShortSerializer.class);
        this.serializers.put(short.class, ShortPrimSerializer.class);
        this.serializers.put(UUID.class, UuidSerializer.class);
    }

    @Override
    public void connect(String uri) throws AlreadyConnectedException {
        if (connected)
            throw new AlreadyConnectedException();
        this.client = new MongoClient(new MongoClientURI(uri));
        this.connected = true;
    }

    @Override
    public void connect(Map<String, String> credentials) throws AlreadyConnectedException {
        if (connected)
            throw new AlreadyConnectedException();
        MongoCredential credential = MongoCredential.createCredential(
                credentials.get("username"),
                credentials.get("name"),
                credentials.get("password").toCharArray()
        );
        this.client = new MongoClient(new ServerAddress(credentials.get("host"), Integer.parseInt(credentials.get("port"))), Collections.singletonList(credential));
        this.database =  this.client.getDatabase(credentials.get("name"));
        this.connected = true;
    }

    @Override
    public void registerSerializer(Class<?> serializedObjectClass, Class<? extends Serializer> serializerClass) {
        this.serializers.put(serializedObjectClass, serializerClass);
    }

    @Override
    public void initModel(Class<?> modelClass) throws ModelAnnotationMissingException {
        if (!modelClass.isAnnotationPresent(Model.class))
            throw new ModelAnnotationMissingException();
        String tableName = modelClass.getAnnotation(Model.class).tableName();
        if (!this.database.listCollectionNames().into(new ArrayList<>()).contains(tableName))
            this.database.createCollection(tableName);
        this.collections.put(modelClass ,this.database.getCollection(tableName));

    }

    @Override
    public void fetchAll(Class<?> modelClass) throws ModelAnnotationMissingException, NotConnectedToDatabaseException, ModelNotInitializedException, NoSerializerFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (!connected)
            throw new NotConnectedToDatabaseException();
        if (!modelClass.isAnnotationPresent(Model.class))
            throw new ModelAnnotationMissingException();
        String tableName = modelClass.getAnnotation(Model.class).tableName();
        MongoCollection<Document> collection = this.collections.get(modelClass);
        if (collection == null)
            throw new ModelNotInitializedException();
        this.deserialize(modelClass, collection.find());
    }

    @Override
    public <T> void fetchMatching(Class<T> modelClass, String key, Object value) throws NotConnectedToDatabaseException, ModelNotInitializedException, ModelAnnotationMissingException, NoSerializerFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (!connected)
            throw new NotConnectedToDatabaseException();
        if (!modelClass.isAnnotationPresent(Model.class))
            throw new ModelAnnotationMissingException();
        String tableName = modelClass.getAnnotation(Model.class).tableName();
        MongoCollection<Document> collection = this.collections.get(modelClass);
        if (collection == null)
            throw new ModelNotInitializedException();
        String serialized = null;
        Class<?> valueSerializer = this.serializers.get(value.getClass());
        if (value.getClass().isAssignableFrom(Map.class))
            valueSerializer =  MapSerializer.class;
        if (valueSerializer == null) {
            throw new NoSerializerFoundException();
        }
        boolean found = false;
        String methodName = "serialize";
        if (Map.class.isAssignableFrom(value.getClass()))
            methodName+="Map";
        for (Method declaredMethod : valueSerializer.getDeclaredMethods()) {
            if (declaredMethod.getName().equals(methodName)) {
                found = true;
                if (Map.class.isAssignableFrom(value.getClass()))
                    serialized = (String) declaredMethod.invoke(valueSerializer.newInstance(), value, this.serializers, value.getClass());
                else
                    serialized = (String) declaredMethod.invoke(valueSerializer.newInstance(), value);
            }
        }
        if (!found || serialized == null)
            throw new NoSerializerFoundException();
        this.deserialize(modelClass, collection.find(new Document(key, serialized)));
    }



    protected <T> void deserialize(Class<T> modelClass, FindIterable<Document> receivedObjects) throws NoSerializerFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NotConnectedToDatabaseException {
        for (Document receivedObject : receivedObjects) {
            boolean toBeFixed = false;
            T initializedClass = modelClass.newInstance();
            for (Field declaredField : modelClass.getDeclaredFields()) {
                boolean found = false;
                if (declaredField.isAnnotationPresent(ModelField.class)) {
                    declaredField.setAccessible(true);
                    Class<? extends Serializer> serializer = this.serializers.get(declaredField.getType());
                    if (Map.class.isAssignableFrom(declaredField.getType()))
                        serializer =  MapSerializer.class;
                    if (serializer == null) {
                        throw new NoSerializerFoundException();
                    }
                    String name = declaredField.getAnnotation(ModelField.class).name();
                    if (name.equals("")) {
                        name = declaredField.getName();
                    }
                    Object value = receivedObject.get(name);
                    if (value == null) {
                        value = declaredField.get(initializedClass);
                        toBeFixed = true;
                    }
                    String methodName = "deserialize";
                    if (Map.class.isAssignableFrom(declaredField.getType()))
                        methodName+="Map";
                    for (Method declaredMethod : serializer.getDeclaredMethods()) {
                        if (declaredMethod.getName().equals(methodName)) {
                            found = true;
                            Object deserialized;
                            if (Map.class.isAssignableFrom(declaredField.getType()))
                                deserialized = declaredMethod.invoke(serializer.newInstance(), value, this.serializers, declaredField.getType());
                            else
                                deserialized = declaredMethod.invoke(serializer.newInstance(), value);
                            declaredField.set(initializedClass, deserialized);
                        }
                    }
                    if (!found)
                        throw new NoSerializerFoundException();
                }
            }
            this.objects.put(initializedClass, modelClass);
            if (toBeFixed)
                this.save(initializedClass, modelClass);
        }
    }

    @Override
    public void save(Object object, Class<?> modelClass) throws NoSerializerFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NotConnectedToDatabaseException {
        if (!connected)
            throw new NotConnectedToDatabaseException();
        Document document = new Document();
        Document key = new Document();
        for (Field declaredField : modelClass.getDeclaredFields()) {
            if (declaredField.isAnnotationPresent(ModelField.class)) {
                Class<? extends Serializer> serializer = this.serializers.get(declaredField.getType());
                if (Map.class.isAssignableFrom(declaredField.getType()))
                    serializer =  MapSerializer.class;
                if (serializer == null) {
                    throw new NoSerializerFoundException();
                }
                declaredField.setAccessible(true);
                Object value = declaredField.get(object);
                boolean found = false;
                String methodName = "serialize";
                if (Map.class.isAssignableFrom(declaredField.getType()))
                    methodName+="Map";
                for (Method declaredMethod : serializer.getDeclaredMethods()) {
                    if (declaredMethod.getName().equals(methodName)) {
                        found = true;
                        String name = declaredField.getAnnotation(ModelField.class).name();
                        if (name.equals("")) {
                            name = declaredField.getName();
                        }
                        String serialized;
                        if (Map.class.isAssignableFrom(declaredField.getType()))
                            serialized = (String) declaredMethod.invoke(serializer.newInstance(), value, this.serializers);
                        else
                            serialized = (String) declaredMethod.invoke(serializer.newInstance(), value);
                        document.put(name, serialized);
                        if (declaredField.getAnnotation(ModelField.class).isPrimaryKey())
                            key.put(name, serialized);
                    }
                }
                if (!found)
                    throw new NoSerializerFoundException();
            }
        }
        this.collections.get(modelClass).updateOne(key, new Document("$set", document), new UpdateOptions().upsert( true ));
        this.objects.put(object, modelClass);
    }

    @Override
    public <T> List<T> readAll(Class<T> modelClass) {
        return this.objects.keySet().stream().filter(k -> k.getClass().equals(modelClass)).map(k -> (T) k).collect(Collectors.toList());
    }

    @Override
    public <T> List<T> filter(Class<T> modelClass, String field, FilterExpression expression, Object value) {
        Stream<?> objects = this.objects.keySet().stream();
        switch (expression) {
            case GREATER_THAN:
                return objects.filter(o -> {
                    try {
                        Field _field = o.getClass().getDeclaredField(field);
                        _field.setAccessible(true);
                        return Long.parseLong(_field.get(o).toString()) > Long.parseLong(value.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }).map(k -> (T) k).collect(Collectors.toList());
            case LESS_THAN:
                return objects.filter(o -> {
                    try {
                        Field _field = o.getClass().getDeclaredField(field);
                        _field.setAccessible(true);
                        return Long.parseLong(_field.get(o).toString()) < Long.parseLong(value.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }).map(k -> (T) k).collect(Collectors.toList());
            case EQUALS:
                return objects.filter(o -> {
                    try {
                        Field _field = o.getClass().getDeclaredField(field);
                        _field.setAccessible(true);
                        return _field.get(o).equals(value);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }).map(k -> (T) k).collect(Collectors.toList());
            case NOT_EQUALS:
                return objects.filter(o -> {
                    try {
                        Field _field = o.getClass().getDeclaredField(field);
                        _field.setAccessible(true);
                        return !_field.get(o).equals(value);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }).map(k -> (T) k).collect(Collectors.toList());
            case GREATER_THAN_EQUALS:
                return objects.filter(o -> {
                    try {
                        Field _field = o.getClass().getDeclaredField(field);
                        _field.setAccessible(true);
                        return Long.parseLong(_field.get(o).toString()) >= Long.parseLong(value.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }).map(k -> (T) k).collect(Collectors.toList());
            case LESS_THAN_EQUALS:
                return objects.filter(o -> {
                    try {
                        Field _field = o.getClass().getDeclaredField(field);
                        _field.setAccessible(true);
                        return Long.parseLong(_field.get(o).toString()) <= Long.parseLong(value.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }).map(k -> (T) k).collect(Collectors.toList());
            default:
                return new LinkedList<>();
        }
    }

    @Override
    public boolean isConnectionValid() {
        try {
            this.client.getAddress();
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    @Override
    public void terminateConnection() throws NotConnectedToDatabaseException {
        if (connected) {
            this.client.close();
            this.connected = false;
        }
        else
            throw new NotConnectedToDatabaseException();
    }
}
