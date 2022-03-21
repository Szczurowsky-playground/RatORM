package pl.szczurowsky.ratorm.serializers;

import java.util.UUID;

public class UuidSerializer implements Serializer<UUID> {

    @Override
    public String serialize(Object providedObject) {
        return UUID.fromString(String.valueOf(providedObject)).toString();
    }

    @Override
    public UUID deserialize(String receivedUUID) {
        return UUID.fromString(receivedUUID);
    }
}
