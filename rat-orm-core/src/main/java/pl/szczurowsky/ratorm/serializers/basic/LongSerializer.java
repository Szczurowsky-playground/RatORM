package pl.szczurowsky.ratorm.serializers.basic;

import pl.szczurowsky.ratorm.serializers.Serializer;

public class LongSerializer implements Serializer<Long> {

    @Override
    public String serialize(Long providedObject) {
        return String.valueOf(providedObject);
    }

    @Override
    public Long deserialize(String receivedLong) {
        return Long.parseLong(receivedLong);
    }
}
