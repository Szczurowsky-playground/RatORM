package pl.szczurowsky.ratorm.serializers.basic;

import pl.szczurowsky.ratorm.exceptions.SerializerException;
import pl.szczurowsky.ratorm.serializers.Serializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeSerializer implements Serializer<LocalDateTime> {
    @Override
    public String serialize(LocalDateTime localDateTime) throws SerializerException {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss").format(localDateTime);
    }

    @Override
    public LocalDateTime deserialize(String s) throws ClassNotFoundException {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss").parse(s, LocalDateTime::from);
    }
}
