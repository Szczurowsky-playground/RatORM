package pl.szczurowsky.ratorm.serializers;

import pl.szczurowsky.ratorm.exception.SerializerException;

/**
 * Interface for serializers which provide easy data-saving custom data-types to database
 * @param <T> Type of serialized/deserialize object
 */
public interface Serializer<T> {

    /**
     * Serialize provided object to string
     * @param providedObject Object
     * @return string to store in database
     * @throws SerializerException Wasn't able to serialize object
     */
    String serialize(Object providedObject) throws SerializerException;

    /**
     * Deserialize provided string to T
     * @param receivedObject Serialized object
     * @return Deserialized object
     */
    T deserialize(String receivedObject) throws ClassNotFoundException;
}
