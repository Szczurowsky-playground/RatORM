package pl.szczurowsky.ratorm.serializers.basic;

import pl.szczurowsky.ratorm.serializers.Serializer;

public class BooleanSerializer implements Serializer<Boolean> {

    @Override
    public String serialize(Boolean providedObject) {
        return String.valueOf(providedObject);
    }

    @Override
    public Boolean deserialize(String receivedBool) {
        return Boolean.parseBoolean(receivedBool);
    }
}
