package pl.szczurowsky.ratorm.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import pl.szczurowsky.ratorm.Model.BaseModel;
import pl.szczurowsky.ratorm.annotation.Model;
import pl.szczurowsky.ratorm.annotation.ModelField;
import pl.szczurowsky.ratorm.database.Database;
import pl.szczurowsky.ratorm.enums.FilterExpression;
import pl.szczurowsky.ratorm.exception.*;
import pl.szczurowsky.ratorm.serializers.*;
import pl.szczurowsky.ratorm.serializers.basic.*;

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
    private final HashMap<Class<?>, Class<? extends Serializer>> serializers = new HashMap<>();
    private final HashMap<Object, Class<? extends BaseModel>> cachedObjects = new HashMap<>();
    private final HashMap<Class<? extends BaseModel>, MongoCollection<Document>> collections = new HashMap<>();
    private boolean connected;

    /**
     * Register default serializers
     */
    public MongoDB() {
        this.serializers.put(String.class, StringSerializer.class);
        this.serializers.put(Character.class, CharacterSerializer.class);
        this.serializers.put(char.class, CharacterSerializer.class);
        this.serializers.put(Integer.class, IntegerSerializer.class);
        this.serializers.put(int.class, IntegerSerializer.class);
        this.serializers.put(Long.class, LongSerializer.class);
        this.serializers.put(long.class, LongSerializer.class);
        this.serializers.put(BigInteger.class, BigIntSerializer.class);
        this.serializers.put(Float.class, FloatSerializer.class);
        this.serializers.put(float.class, FloatSerializer.class);
        this.serializers.put(Boolean.class, BooleanSerializer.class);
        this.serializers.put(boolean.class, BooleanSerializer.class);
        this.serializers.put(Double.class, DoubleSerializer.class);
        this.serializers.put(double.class, DoubleSerializer.class);
        this.serializers.put(Short.class, ShortSerializer.class);
        this.serializers.put(short.class, ShortSerializer.class);
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

    @SafeVarargs
    @Override
    public final void initModel(Class<? extends BaseModel>... modelClasses) throws ModelAnnotationMissingException, MoreThanOnePrimaryKeyException, NoPrimaryKeyException {
        for (Class<? extends BaseModel> modelClass : modelClasses) {
            if (!modelClass.isAnnotationPresent(Model.class))
                throw new ModelAnnotationMissingException();
            int primaryKeys = 0;
            for (Field declaredField : modelClass.getDeclaredFields()) {
                if(declaredField.isAnnotationPresent(ModelField.class))
                    if (declaredField.getAnnotation(ModelField.class).isPrimaryKey())
                        primaryKeys++;
            }
            if (primaryKeys == 0)
                throw new NoPrimaryKeyException();
            else if (primaryKeys != 1)
                throw new MoreThanOnePrimaryKeyException();
            String tableName = modelClass.getAnnotation(Model.class).tableName();
            if (!this.database.listCollectionNames().into(new ArrayList<>()).contains(tableName))
                this.database.createCollection(tableName);
            this.collections.put(modelClass ,this.database.getCollection(tableName));
            if (modelClass.getAnnotation(Model.class).cached() && modelClass.getAnnotation(Model.class).autoFetch()) {
                try {
                    this.fetchAll(modelClass);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public <T extends BaseModel> List<T> fetchAll(Class<T> modelClass) throws ModelAnnotationMissingException, NotConnectedToDatabaseException, ModelNotInitializedException, NoSerializerFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (!connected)
            throw new NotConnectedToDatabaseException();
        if (!modelClass.isAnnotationPresent(Model.class))
            throw new ModelAnnotationMissingException();
        String tableName = modelClass.getAnnotation(Model.class).tableName();
        MongoCollection<Document> collection = this.collections.get(modelClass);
        if (collection == null)
            throw new ModelNotInitializedException();
        return this.deserialize(modelClass, collection.find());
    }

    @Override
    public <T extends BaseModel> List<T> fetchMatching(Class<T> modelClass, String key, Object value) throws NotConnectedToDatabaseException, ModelNotInitializedException, ModelAnnotationMissingException, NoSerializerFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
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
                    serialized = (String) declaredMethod.invoke(valueSerializer.newInstance(), value, this.serializers);
                else
                    serialized = (String) declaredMethod.invoke(valueSerializer.newInstance(), value);
            }
        }
        if (!found || serialized == null)
            throw new NoSerializerFoundException();
       return this.deserialize(modelClass, collection.find(new Document(key, serialized)));
    }



    protected <T extends BaseModel> List<T> deserialize(Class<T> modelClass, FindIterable<Document> receivedObjects) throws NoSerializerFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NotConnectedToDatabaseException {
        List<T> deserializedObjects = new LinkedList<>();
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
                    else if (Collection.class.isAssignableFrom(declaredField.getType()))
                        serializer = CollectionSerializer.class;
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
                    else if (Collection.class.isAssignableFrom(declaredField.getType()))
                        methodName+="Collection";
                    for (Method declaredMethod : serializer.getDeclaredMethods()) {
                        if (declaredMethod.getName().equals(methodName)) {
                            found = true;
                            Object deserialized;
                            if (Map.class.isAssignableFrom(declaredField.getType()) || Collection.class.isAssignableFrom(declaredField.getType()))
                                deserialized = declaredMethod.invoke(serializer.newInstance(), value, this.serializers);
                            else
                                deserialized = declaredMethod.invoke(serializer.newInstance(), value);
                            declaredField.set(initializedClass, deserialized);
                        }
                    }
                    if (!found)
                        throw new NoSerializerFoundException();
                }
            }
            if (modelClass.getAnnotation(Model.class).cached())
                this.cachedObjects.put(initializedClass, modelClass);
            deserializedObjects.add(initializedClass);
            if (toBeFixed)
                this.save(initializedClass, modelClass);
        }
        return deserializedObjects;
    }

    @Override
    public <T extends BaseModel> void save(T object, Class<T> modelClass) throws NoSerializerFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NotConnectedToDatabaseException {
        if (!connected)
            throw new NotConnectedToDatabaseException();
        Document document = new Document();
        Document key = new Document();
        object.lockWrite();
        for (Field declaredField : modelClass.getDeclaredFields()) {
            if (declaredField.isAnnotationPresent(ModelField.class)) {
                Class<? extends Serializer> serializer = this.serializers.get(declaredField.getType());
                if (Map.class.isAssignableFrom(declaredField.getType()))
                    serializer =  MapSerializer.class;
                else if (Collection.class.isAssignableFrom(declaredField.getType()))
                    serializer = CollectionSerializer.class;
                if (serializer == null) {
                    throw new NoSerializerFoundException();
                }
                declaredField.setAccessible(true);
                Object value = declaredField.get(object);
                boolean found = false;
                String methodName = "serialize";
                if (Map.class.isAssignableFrom(declaredField.getType()))
                    methodName+="Map";
                else if (Collection.class.isAssignableFrom(declaredField.getType()))
                    methodName+="Collection";
                for (Method declaredMethod : serializer.getDeclaredMethods()) {
                    if (declaredMethod.getName().equals(methodName)) {
                        found = true;
                        String name = declaredField.getAnnotation(ModelField.class).name();
                        if (name.equals("")) {
                            name = declaredField.getName();
                        }
                        String serialized;
                        if (Map.class.isAssignableFrom(declaredField.getType()) || Collection.class.isAssignableFrom(declaredField.getType()))
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
        if (modelClass.getAnnotation(Model.class).cached())
            this.cachedObjects.put(object, modelClass);
        object.unlockWrite();
    }

    @Override
    public <T extends BaseModel> List<T> filter(Class<T> modelClass, String field, FilterExpression expression, Object value, Stream<T> objects) {
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
    public <T extends BaseModel> void delete(T object, Class<T> modelClass) throws NotConnectedToDatabaseException, NoSerializerFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {
        if (!connected)
            throw new NotConnectedToDatabaseException();
        object.lockWrite();
        if (modelClass.getAnnotation(Model.class).cached())
            this.cachedObjects.remove(object);
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
                        if (declaredField.getAnnotation(ModelField.class).isPrimaryKey())
                            key.put(name, serialized);
                    }
                }
                if (!found)
                    throw new NoSerializerFoundException();
            }
        }
        this.collections.get(modelClass).deleteOne(key);
        object.unlockWrite();
    }

    @Override
    public <T extends BaseModel> List<T> readAllFromCache(Class<T> modelClass) throws NotCachedException {
        if (!modelClass.getAnnotation(Model.class).cached())
            throw new NotCachedException();
        return this.cachedObjects.keySet().stream().filter(k -> k.getClass().equals(modelClass)).map(k -> (T) k).collect(Collectors.toList());
    }

    @Override
    public <T extends BaseModel> List<T> readMatchingFromCache(Class<T> modelClass, String field, Object value) throws NotCachedException {
        if (!modelClass.getAnnotation(Model.class).cached())
            throw new NotCachedException();
        return this.filter(modelClass, field, FilterExpression.EQUALS, value, this.readAllFromCache(modelClass).stream());
    }

    @Override
    public void updateWholeCache(Object object, Class<? extends BaseModel> modelClass) throws NotCachedException, NoSerializerFoundException, NotConnectedToDatabaseException, ModelNotInitializedException, InvocationTargetException, ModelAnnotationMissingException, InstantiationException, IllegalAccessException {
        if (!modelClass.getAnnotation(Model.class).cached())
            throw new NotCachedException();
        for (Object o : this.cachedObjects.keySet()) {
            if (o.getClass().equals(modelClass))
                this.cachedObjects.remove(o);
        }
        this.fetchAll(modelClass);
    }

    @Override
    public <T extends BaseModel> void updateMatchingCache(Class<T> modelClass, String key, Object value) throws NotCachedException, NoSerializerFoundException, NotConnectedToDatabaseException, ModelNotInitializedException, InvocationTargetException, ModelAnnotationMissingException, InstantiationException, IllegalAccessException {
        if (!modelClass.getAnnotation(Model.class).cached())
            throw new NotCachedException();
        List<T> matchingObjects = this.readMatchingFromCache(modelClass, key, value);
        for (Object o : this.cachedObjects.keySet()) {
            if (matchingObjects.contains(o))
                this.cachedObjects.remove(o);
        }
        this.fetchMatching(modelClass, key, value);
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
