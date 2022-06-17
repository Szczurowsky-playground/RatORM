package pl.szczurowsky.ratorm.serializers.complex;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import pl.szczurowsky.ratorm.serializers.ComplexSerializer;
import pl.szczurowsky.ratorm.serializers.Serializer;
import pl.szczurowsky.ratorm.serializers.basic.BooleanSerializer;
import pl.szczurowsky.ratorm.serializers.basic.StringSerializer;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.fail;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MapSerializerTest {

    protected final HashMap<Class<?>, Class<? extends Serializer<?>>> serializers = new HashMap<>();

    protected final HashMap<Class<?>, Class<? extends ComplexSerializer>> complexSerializers = new HashMap<>();

    /**
     * Register serializers
     */
    @BeforeAll
    public void setupSerializers() {
        this.serializers.put(String.class, StringSerializer.class);
        this.serializers.put(Boolean.class, BooleanSerializer.class);

        this.complexSerializers.put(AbstractMap.class, MapSerializer.class);
    }

    /**
     * Test serializing of map with basic types
     * and complex types
     */
    @Test
    public void TestMapSerializer() {
        MapSerializer mapSerializer = new MapSerializer();
        Map<String, Boolean> map = new HashMap<>();
        Map<String, Map<String, Boolean>> map2 = new HashMap<>();
        map.put("test", true);
        map.put("test2", false);
        map2.put("test", map);
        try {
            Assertions.assertEquals(map, mapSerializer.deserialize(mapSerializer.serialize(map, serializers, complexSerializers), serializers, complexSerializers));
            Assertions.assertEquals(map2, mapSerializer.deserialize(mapSerializer.serialize(map2, serializers, complexSerializers), serializers, complexSerializers));
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

}