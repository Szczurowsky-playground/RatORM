package pl.szczurowsky.ratorm.serializers.basic;

import pl.szczurowsky.ratorm.serializers.Serializer;

public class FloatSerializer implements Serializer<Float> {

    @Override
    public String serialize(Object providedObject) {
        return String.valueOf(providedObject);
    }

    @Override
    public Float deserialize(String receivedFloat) {
        return Float.parseFloat(receivedFloat);
    }
}
