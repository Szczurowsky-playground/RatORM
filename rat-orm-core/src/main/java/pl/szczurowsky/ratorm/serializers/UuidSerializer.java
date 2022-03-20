package pl.szczurowsky.ratorm.serializers;

import java.util.UUID;

public class UuidSerializer implements Serializer {

    @Override
    public String serialize(Object providedObject) {
        return UUID.fromString(String.valueOf(providedObject)).toString();
    }

    public UUID deserialize(String receivedUUID) {
        return UUID.fromString(receivedUUID);
    }
}
