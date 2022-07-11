package pl.szczurowsky.ratorm.serializers.basic;

import pl.szczurowsky.ratorm.serializers.Serializer;

public class DoubleSerializer implements Serializer<Double> {

    @Override
    public String serialize(Double providedObject) {
        return String.valueOf(providedObject);
    }

    @Override
    public Double deserialize(String receivedDouble) {
        return Double.valueOf(receivedDouble);
    }
}
