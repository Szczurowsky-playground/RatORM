package pl.szczurowsky.ratorm.serializers;

import pl.szczurowsky.ratorm.exception.SerializerException;

public interface Serializer {

    /**
     * Serialize provided object to string
     * @param providedObject Object
     * @return string to store in database
     * * @throws SerializerException Exception thrown when something goes wrong in serialization
     */
    String serialize(Object providedObject) throws SerializerException;

}
