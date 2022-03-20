package pl.szczurowsky.ratorm.serializers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.szczurowsky.ratorm.exception.SerializerException;
import pl.szczurowsky.ratorm.serializers.basic.IntegerSerializer;
import pl.szczurowsky.ratorm.serializers.basic.StringSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SerializerTest {

    @Test
    public void testStringSerialization() {
        StringSerializer serializer = new StringSerializer();
        String s = "qwertyuiop";
        String serialize = serializer.serialize(s);
        Assertions.assertEquals(s, serializer.deserialize(serialize));
    }

    @Test
    public void testUuidSerialization() {
        UuidSerializer serializer = new UuidSerializer();
        UUID uuid = UUID.randomUUID();
        String serialized = serializer.serialize(uuid);
        Assertions.assertEquals(uuid, serializer.deserialize(serialized));
    }

    @Test
    public void testMapSerialization() throws SerializerException, ClassNotFoundException {
        MapSerializer serializer = new MapSerializer();
        HashMap<Class<?>, Class<? extends Serializer>> serializers = new HashMap<>();
        serializers.put(String.class, StringSerializer.class);
        serializers.put(Integer.class, IntegerSerializer.class);
        Map<String, Integer> map = new HashMap<>();
        map.put("Test", 1);
        map.put("Test2", 2);
        String serializedMap = serializer.serializeMap(map, serializers);
        Map<String, Integer> map2 = serializer.deserializeMap(serializedMap, serializers);
        for (String s : map.keySet()) {
            System.out.println(s);
        }
        for (String s : map2.keySet()) {
            System.out.println(s);
        }
        Assertions.assertEquals(map2, map);
    }

}
