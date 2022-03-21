package pl.szczurowsky.ratorm.serializers.basic;

import pl.szczurowsky.ratorm.serializers.Serializer;

public class CharacterSerializer implements Serializer<Character> {

    @Override
    public String serialize(Object providedObject) {
        return String.valueOf(providedObject);
    }

    @Override
    public Character deserialize(String receivedChar) {
        return receivedChar.charAt(0);
    }
}
