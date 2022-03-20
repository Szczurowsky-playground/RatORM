package pl.szczurowsky.ratorm.serializers;

public class StringSerializer implements Serializer {

    @Override
    public String serialize(Object providedObject) {
        return String.valueOf(providedObject);
    }

    public String deserialize(String receivedString) {
        return receivedString;
    }
}
