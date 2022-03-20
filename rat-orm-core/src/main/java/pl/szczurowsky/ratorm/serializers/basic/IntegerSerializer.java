package pl.szczurowsky.ratorm.serializers.basic;

import pl.szczurowsky.ratorm.serializers.Serializer;

public class IntegerSerializer implements Serializer {

    @Override
    public String serialize(Object providedObject) {
        return String.valueOf(providedObject);
    }

    public Integer deserialize(String receivedInteger) {
        return Integer.parseInt(receivedInteger);
    }
}
