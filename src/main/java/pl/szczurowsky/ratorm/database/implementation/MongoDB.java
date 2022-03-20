package pl.szczurowsky.ratorm.database.implementation;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import pl.szczurowsky.ratorm.annotation.DatabaseField;
import pl.szczurowsky.ratorm.annotation.Model;
import pl.szczurowsky.ratorm.database.Database;
import pl.szczurowsky.ratorm.enums.FilterExpression;
import pl.szczurowsky.ratorm.exception.AlreadyConnectedException;
import pl.szczurowsky.ratorm.exception.ModelAnnotationMissingException;
import pl.szczurowsky.ratorm.exception.NoSerializerFoundException;
import pl.szczurowsky.ratorm.exception.NotConnectedToDatabaseException;
import pl.szczurowsky.ratorm.serializers.Serializer;
import pl.szczurowsky.ratorm.serializers.StringSerializer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MongoDB implements Database {

    private MongoClient client;
    private MongoDatabase database;
    private final HashMap<Object, Class<?>> objects = new HashMap<>();
    private final HashMap<Class<?>, Class<? extends Serializer>> serializers = new HashMap<>();
    private final HashMap<Class<?>, MongoCollection> collections = new HashMap<>();
    private boolean connected;

    /**
     * Register default serializers
     */
    public MongoDB() {
        this.serializers.put(String.class, StringSerializer.class);
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
    public void initModel(Class<?> modelClass) throws ModelAnnotationMissingException {
        if (!modelClass.isAnnotationPresent(Model.class))
            throw new ModelAnnotationMissingException();
        String tableName = modelClass.getAnnotation(Model.class).tableName();
        if (!this.database.listCollectionNames().into(new ArrayList<>()).contains(tableName))
            this.database.createCollection(tableName);
        this.collections.put(modelClass ,this.database.getCollection(tableName));
    }

    @Override
    public void save(Object object, Class<?> modelClass) throws NoSerializerFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Document document = new Document();
        Document key = new Document();
        for (Field declaredField : modelClass.getDeclaredFields()) {
            if (declaredField.isAnnotationPresent(DatabaseField.class)) {
                Class<? extends Serializer> serializer = this.serializers.get(declaredField.getAnnotation(DatabaseField.class).type());
                if (serializer == null) {
                    throw new NoSerializerFoundException();
                }
                declaredField.setAccessible(true);
                Object value = declaredField.get(object);
                boolean found = false;
                for (Method declaredMethod : serializer.getDeclaredMethods()) {
                    if (declaredMethod.getName().equals("serialize")) {
                        found = true;
                        String serialized = (String) declaredMethod.invoke(serializer.newInstance(), value, modelClass);
                        document.put(declaredField.getAnnotation(DatabaseField.class).name(), serialized);
                        if (declaredField.getAnnotation(DatabaseField.class).isPrimaryKey())
                            key.put(declaredField.getAnnotation(DatabaseField.class).name(), serialized);
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
