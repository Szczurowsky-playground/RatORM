package pl.szczurowsky.ratorm.serializers.basic;

import pl.szczurowsky.ratorm.serializers.Serializer;

public class IntegerSerializer implements Serializer<Integer> {

    @Override
    public String serialize(Object providedObject) {
        return String.valueOf(providedObject);
    }

    @Override
    public Integer deserialize(String receivedInteger) {
        return Integer.parseInt(receivedInteger);
    }
}
