package pl.szczurowsky.ratorm.serializers.basic;

import pl.szczurowsky.ratorm.exceptions.SerializerException;
import pl.szczurowsky.ratorm.serializers.Serializer;

import java.util.UUID;

public class UUIDSerializer implements Serializer<UUID> {

    @Override
    public String serialize(java.util.UUID providedObject) throws SerializerException {
        return providedObject.toString();
    }

    @Override
    public UUID deserialize(String receivedObject) throws ClassNotFoundException {
        return java.util.UUID.fromString(receivedObject);
    }
}
