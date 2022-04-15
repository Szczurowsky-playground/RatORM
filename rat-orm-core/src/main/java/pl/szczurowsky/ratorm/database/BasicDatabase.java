package pl.szczurowsky.ratorm.database;

import operation.OperationManager;
import pl.szczurowsky.ratorm.Model.BaseModel;
import pl.szczurowsky.ratorm.annotation.Model;
import pl.szczurowsky.ratorm.enums.FilterExpression;
import pl.szczurowsky.ratorm.exception.*;
import pl.szczurowsky.ratorm.serializers.BigIntSerializer;
import pl.szczurowsky.ratorm.serializers.EnumSerializer;
import pl.szczurowsky.ratorm.serializers.Serializer;
import pl.szczurowsky.ratorm.serializers.UuidSerializer;
import pl.szczurowsky.ratorm.serializers.basic.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class BasicDatabase implements Database {

    /**
     * Map of all models and their serializers
     */
    protected final HashMap<Class<?>, Class<? extends Serializer>> serializers = new HashMap<>();

    /**
     * Cached objects
     */
    protected final HashMap<Object, Class<? extends BaseModel>> cachedObjects = new HashMap<>();

    /**
     * Map of all models and their operations
     */
    protected final OperationManager operationManager = new OperationManager();

    /**
     * Is connected to database
     */
    protected boolean connected;


    /**
     * Register default serializers
     */
    public BasicDatabase() {
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
        this.serializers.put(Enum.class, EnumSerializer.class);
    }

    @Override
    public OperationManager getOperationManager() {
        return operationManager;
    }

    @Override
    public void registerSerializer(Class<?> serializedObjectClass, Class<? extends Serializer> serializerClass) {
        this.serializers.put(serializedObjectClass, serializerClass);
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


}
