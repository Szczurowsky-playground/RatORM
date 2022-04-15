package pl.szczurowsky.ratorm.serializers;

import pl.szczurowsky.ratorm.exception.SerializerException;

public class EnumSerializer implements Serializer<Enum> {
    @Override
    public String serialize(Object providedObject) throws SerializerException {
        return String.valueOf(providedObject) + " " + String.valueOf(providedObject.getClass()).replace("class ", "");
    }

    @Override
    public Enum deserialize(String receivedObject) throws ClassNotFoundException {
        String[] splitted = receivedObject.split(" ");
        return Enum.valueOf((Class<Enum>) Class.forName(splitted[1]), splitted[0]   );
    }
}
