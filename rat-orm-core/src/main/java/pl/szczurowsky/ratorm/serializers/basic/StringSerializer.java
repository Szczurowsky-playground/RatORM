package pl.szczurowsky.ratorm.serializers.basic;

import pl.szczurowsky.ratorm.serializers.Serializer;

public class StringSerializer implements Serializer<String> {

    @Override
    public String serialize(String providedObject) {
        return String.valueOf(providedObject);
    }

    @Override
    public String deserialize(String receivedString) {
        return receivedString;
    }
}
