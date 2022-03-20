package pl.szczurowsky.ratorm.serializers.primitive;

import pl.szczurowsky.ratorm.serializers.Serializer;

public class LongPrimSerializer implements Serializer {

    @Override
    public String serialize(Object providedObject) {
        return String.valueOf(providedObject);
    }

    public long deserialize(String receivedLong) {
        return Long.parseLong(receivedLong);
    }
}
