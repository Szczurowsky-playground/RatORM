package pl.szczurowsky.ratorm.serializers.basic;

import pl.szczurowsky.ratorm.serializers.Serializer;

public class FloatSerializer implements Serializer {

    @Override
    public String serialize(Object providedObject) {
        return String.valueOf(providedObject);
    }

    public Float deserialize(String receivedFloat) {
        return Float.parseFloat(receivedFloat);
    }
}
