package pl.szczurowsky.ratorm.serializers.primitive;

import pl.szczurowsky.ratorm.serializers.Serializer;

public class BoolSerializer implements Serializer {

    @Override
    public String serialize(Object providedObject) {
        return String.valueOf(providedObject);
    }

    public boolean deserialize(String receivedBool) {
        return Boolean.parseBoolean(receivedBool);
    }
}
