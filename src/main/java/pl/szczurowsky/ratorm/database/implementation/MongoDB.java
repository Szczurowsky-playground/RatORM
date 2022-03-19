package pl.szczurowsky.ratorm.database.implementation;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import pl.szczurowsky.ratorm.annotation.Model;
import pl.szczurowsky.ratorm.database.Database;
import pl.szczurowsky.ratorm.enums.FilterExpression;
import pl.szczurowsky.ratorm.exception.AlreadyConnectedException;
import pl.szczurowsky.ratorm.exception.ModelAnnotationMissingException;
import pl.szczurowsky.ratorm.exception.NotConnectedToDatabaseException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class MongoDB implements Database {

    private MongoClient client;
    private MongoDatabase database;
    private final HashMap<Object, Class<?>> objects = new HashMap<>();
    private final ArrayList<MongoCollection> collections = new ArrayList<>();
    private boolean connected;

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
        this.collections.add(this.database.getCollection(tableName));
    }

    @Override
    public void save(Object object, Class<?> modelClass) {
        this.objects.put(object, modelClass);
    }

    @Override
    public Object[] readAll(Class<?> modelClass) {
        return this.objects.keySet().stream().filter(k -> k.getClass().equals(modelClass)).toArray();
    }

    @Override
    public Object[] filter(Class<?> modelClass, String field, FilterExpression expression, Object value) {
        Stream<Object> objects = this.objects.keySet().stream();
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
                }).toArray();
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
                }).toArray();
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
                }).toArray();
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
                }).toArray();
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
                }).toArray();
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
                }).toArray();
            default:
                return new Object[0];
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
