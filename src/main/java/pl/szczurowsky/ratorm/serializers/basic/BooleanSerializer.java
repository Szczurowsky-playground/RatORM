package pl.szczurowsky.ratorm.serializers.basic;

import pl.szczurowsky.ratorm.serializers.Serializer;

public class BooleanSerializer implements Serializer {

    @Override
    public String serialize(Object providedObject) {
        return String.valueOf(providedObject);
    }

    public Boolean deserialize(String receivedBool) {
        return Boolean.parseBoolean(receivedBool);
    }
}
