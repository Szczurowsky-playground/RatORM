package pl.szczurowsky.ratorm.serializers.basic;

import pl.szczurowsky.ratorm.exceptions.SerializerException;
import pl.szczurowsky.ratorm.serializers.Serializer;

public class EnumSerializer implements Serializer<Enum> {
    @Override
    public String serialize(Enum providedObject) throws SerializerException {
        return String.valueOf(providedObject) + " " + String.valueOf(providedObject.getClass()).replace("class ", "");
    }

    @Override
    public Enum deserialize(String receivedObject) throws ClassNotFoundException {
        String[] splitted = receivedObject.split(" ");
        return Enum.valueOf((Class<Enum>) Class.forName(splitted[1]), splitted[0]   );
    }
}
