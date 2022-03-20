package pl.szczurowsky.ratorm.serializers.primitive;

import pl.szczurowsky.ratorm.serializers.Serializer;

public class FloatPrimSerializer implements Serializer {

    @Override
    public String serialize(Object providedObject) {
        return String.valueOf(providedObject);
    }

    public float deserialize(String receivedFloat) {
        return Float.parseFloat(receivedFloat);
    }
}
