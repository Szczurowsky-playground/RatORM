package pl.szczurowsky.ratorm.serializers.basic;

import pl.szczurowsky.ratorm.serializers.Serializer;

public class LongSerializer implements Serializer {

    @Override
    public String serialize(Object providedObject) {
        return String.valueOf(providedObject);
    }

    public Long deserialize(String receivedLong) {
        return Long.parseLong(receivedLong);
    }
}
