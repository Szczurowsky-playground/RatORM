package pl.szczurowsky.ratorm.serializers.basic;

import pl.szczurowsky.ratorm.serializers.Serializer;

public class ShortSerializer implements Serializer<Short> {

    @Override
    public String serialize(Short providedObject) {
        return String.valueOf(providedObject);
    }

    @Override
    public Short deserialize(String receivedShort) {
        return Short.valueOf(receivedShort);
    }
}
