package pl.szczurowsky.ratorm.serializers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.szczurowsky.ratorm.exception.SerializerException;
import pl.szczurowsky.ratorm.serializers.basic.*;

import java.math.BigInteger;
import java.util.*;

public class SerializerTest {

    enum TestEnum {
        A, B
    }

    @Test
    public void testBooleanSerialization() {
        BooleanSerializer serializer = new BooleanSerializer();
        Boolean b = true;
        String serialized = serializer.serialize(b);
        Assertions.assertEquals(b, serializer.deserialize(serialized));
    }

    @Test
    public void testCharacterSerialization() {
        CharacterSerializer serializer = new CharacterSerializer();
        Character c = 'a';
        String serialized = serializer.serialize(c);
        Assertions.assertEquals(c, serializer.deserialize(serialized));
    }

    @Test
    public void testDoubleSerialization() {
        DoubleSerializer serializer = new DoubleSerializer();
        Double d = 123.45;
        String serialized = serializer.serialize(d);
        Assertions.assertEquals(d, serializer.deserialize(serialized));
    }

    @Test
    public void testFloatSerialization() {
        FloatSerializer serializer = new FloatSerializer();
        Float f = 123.45f;
        String serialized = serializer.serialize(f);
        Assertions.assertEquals(f, serializer.deserialize(serialized));
    }

    @Test
    public void testIntegerSerialization() {
        IntegerSerializer serializer = new IntegerSerializer();
        Integer i = 123;
        String serialized = serializer.serialize(i);
        Assertions.assertEquals(i, serializer.deserialize(serialized));
    }

    @Test
    public void testLongSerialization() {
        LongSerializer serializer = new LongSerializer();
        Long l = 123L;
        String serialized = serializer.serialize(l);
        Assertions.assertEquals(l, serializer.deserialize(serialized));
    }

    @Test
    public void testShortSerialization() {
        ShortSerializer serializer = new ShortSerializer();
        Short s = 123;
        String serialized = serializer.serialize(s);
        Assertions.assertEquals(s, serializer.deserialize(serialized));
    }

    @Test
    public void testStringSerialization() {
        StringSerializer serializer = new StringSerializer();
        String s = "qwertyuiop";
        String serialize = serializer.serialize(s);
        Assertions.assertEquals(s, serializer.deserialize(serialize));
    }

    @Test
    public void testBigIntSerialization() {
        BigIntSerializer serializer = new BigIntSerializer();
        BigInteger bi = new BigInteger("2137");
        String serialized = serializer.serialize(bi);
        Assertions.assertEquals(bi, serializer.deserialize(serialized));
    }

    @Test
    public void testCollectionSerialization() throws SerializerException {
        CollectionSerializer serializer = new CollectionSerializer();
        HashMap<Class<?>, Class<? extends Serializer>> serializers = new HashMap<>();
        serializers.put(String.class, StringSerializer.class);
        List<String> list = Arrays.asList("qwerty", "asdfgh");
        String serialized = serializer.serializeCollection(list, serializers);
        Assertions.assertEquals(list, serializer.deserializeCollection(serialized, serializers));
    }

    @Test
    public void testEnumSerialization() throws ClassNotFoundException, SerializerException {
        EnumSerializer serializer = new EnumSerializer();
        TestEnum e = TestEnum.A;
        String serialized = serializer.serialize(e);
        Assertions.assertEquals(e, serializer.deserialize(serialized));
        Assertions.assertNotEquals(TestEnum.B, serializer.deserialize(serialized));
    }

    @Test
    public void testMapSerialization() throws SerializerException {
        MapSerializer serializer = new MapSerializer();
        HashMap<Class<?>, Class<? extends Serializer>> serializers = new HashMap<>();
        serializers.put(String.class, StringSerializer.class);
        serializers.put(Integer.class, IntegerSerializer.class);
        Map<String, Integer> map = new HashMap<>();
        map.put("Test", 1);
        map.put("Test2", 2);
        String serializedMap = serializer.serializeMap(map, serializers);
        Map<String, Integer> map2 = serializer.deserializeMap(serializedMap, serializers);
        Assertions.assertEquals(map2, map);
    }

    @Test
    public void testUuidSerialization() {
        UuidSerializer serializer = new UuidSerializer();
        UUID uuid = UUID.randomUUID();
        String serialized = serializer.serialize(uuid);
        Assertions.assertEquals(uuid, serializer.deserialize(serialized));
    }
}

