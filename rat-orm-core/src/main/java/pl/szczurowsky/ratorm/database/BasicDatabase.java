package pl.szczurowsky.ratorm.database;

import operation.OperationManager;
import pl.szczurowsky.ratorm.Model.BaseModel;
import pl.szczurowsky.ratorm.enums.FilterExpression;
import pl.szczurowsky.ratorm.serializers.BigIntSerializer;
import pl.szczurowsky.ratorm.serializers.EnumSerializer;
import pl.szczurowsky.ratorm.serializers.Serializer;
import pl.szczurowsky.ratorm.serializers.UuidSerializer;
import pl.szczurowsky.ratorm.serializers.basic.*;

import java.lang.reflect.Field;
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


}
