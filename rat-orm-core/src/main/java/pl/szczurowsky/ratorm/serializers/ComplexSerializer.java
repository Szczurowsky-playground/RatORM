package pl.szczurowsky.ratorm.serializers;

import pl.szczurowsky.ratorm.exceptions.SerializerException;

import java.util.HashMap;

/**
 * Interface for more complex serializers which provide easy data-saving custom data-types to database
 * Complex serializer can serialize/deserialize multi-layer objects
 * @param <T> Type of serialized/deserialize object
 */
public abstract class ComplexSerializer<T> {

    /**
     * Serialize provided object to string
     * @param providedObject Object
     * @return string to store in database
     * @throws SerializerException Wasn't able to serialize object
     */
    public abstract String serialize(T providedObject, HashMap<Class<?>, Class<? extends Serializer<?>>> serializers, HashMap<Class<?>, Class<? extends ComplexSerializer>> complexSerializers) throws SerializerException;

    /**
     * Deserialize provided string to T
     * @param receivedObject Serialized object
     * @return Deserialized object
     */
    public abstract T deserialize(String receivedObject, HashMap<Class<?>, Class<? extends Serializer<?>>> serializers, HashMap<Class<?>, Class<? extends ComplexSerializer>> complexSerializers) throws ClassNotFoundException, SerializerException;

}
