package pl.szczurowsky.ratorm.serializers.basic;

import pl.szczurowsky.ratorm.exceptions.SerializerException;
import pl.szczurowsky.ratorm.serializers.Serializer;

import java.time.Duration;

public class DurationSerializer implements Serializer<Duration> {

    @Override
    public String serialize(Duration providedObject) throws SerializerException {
        return providedObject.toString();
    }

    @Override
    public Duration deserialize(String receivedObject) throws ClassNotFoundException {
        return Duration.parse(receivedObject);
    }
}
