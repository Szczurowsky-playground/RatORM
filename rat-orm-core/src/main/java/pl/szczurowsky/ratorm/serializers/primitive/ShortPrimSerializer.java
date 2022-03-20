package pl.szczurowsky.ratorm.serializers.primitive;

import pl.szczurowsky.ratorm.serializers.Serializer;

public class ShortPrimSerializer implements Serializer {

    @Override
    public String serialize(Object providedObject) {
        return String.valueOf(providedObject);
    }

    public short deserialize(String receivedShort) {
        return Short.valueOf(receivedShort);
    }
}
