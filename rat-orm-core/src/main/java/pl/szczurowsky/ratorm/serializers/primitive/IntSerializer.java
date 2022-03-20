package pl.szczurowsky.ratorm.serializers.primitive;

import pl.szczurowsky.ratorm.serializers.Serializer;

public class IntSerializer implements Serializer {

    @Override
    public String serialize(Object providedObject) {
        return String.valueOf(providedObject);
    }

    public int deserialize(String receivedInt) {
        return Integer.parseInt(receivedInt);
    }
}
