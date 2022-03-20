package pl.szczurowsky.ratorm.serializers.basic;

import pl.szczurowsky.ratorm.serializers.Serializer;

public class DoubleSerializer implements Serializer {

    @Override
    public String serialize(Object providedObject) {
        return String.valueOf(providedObject);
    }

    public Double deserialize(String receivedDouble) {
        return Double.valueOf(receivedDouble);
    }
}
