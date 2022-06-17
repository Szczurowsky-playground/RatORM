package pl.szczurowsky.ratorm.serializers.basic;

import pl.szczurowsky.ratorm.exceptions.SerializerException;
import pl.szczurowsky.ratorm.serializers.Serializer;

public class ByteSerializer implements Serializer<Byte> {

    @Override
    public String serialize(Byte providedObject) throws SerializerException {
        return providedObject.toString();
    }

    @Override
    public Byte deserialize(String receivedObject) throws ClassNotFoundException {
        return Byte.parseByte(receivedObject);
    }
}
