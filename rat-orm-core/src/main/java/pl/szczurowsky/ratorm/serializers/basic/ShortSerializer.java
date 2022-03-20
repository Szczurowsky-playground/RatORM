package pl.szczurowsky.ratorm.serializers.basic;

import pl.szczurowsky.ratorm.serializers.Serializer;

public class ShortSerializer implements Serializer {

    @Override
    public String serialize(Object providedObject) {
        return String.valueOf(providedObject);
    }

    public Short deserialize(String receivedShort) {
        return Short.valueOf(receivedShort);
    }
}
