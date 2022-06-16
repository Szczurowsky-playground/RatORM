package pl.szczurowsky.ratorm;

import pl.szczurowsky.ratorm.credentials.Credentials;

import java.util.HashMap;

/**
 * Abstraction for database.
 * Provides core methods for all database types
 */
public abstract class Database {

    protected final HashMap<Class<?>, Class<?>> serializers = new HashMap<>();

    protected final HashMap<Class<?>, Class<?>> complexSerializers = new HashMap<>();

    public void registerSerializer(Class<?> classType, Class<?> serializer) {
        serializers.put(classType, serializer);
    }

    public void registerComplexSerializer(Class<?> classType, Class<?> serializer) {
        complexSerializers.put(classType, serializer);
    }

    public abstract void connect(String URI);

    public abstract void connect(Credentials credentials);



}
