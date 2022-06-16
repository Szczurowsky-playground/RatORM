package pl.szczurowsky.ratorm.serializers;

import pl.szczurowsky.ratorm.exceptions.SerializerException;

public interface ComplexSerializer<T> {

    String serialize(T providedObject) throws SerializerException;

    T deserialize(String receivedObject) throws ClassNotFoundException;

}
