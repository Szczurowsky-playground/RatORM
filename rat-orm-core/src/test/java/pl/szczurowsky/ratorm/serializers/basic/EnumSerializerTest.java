package pl.szczurowsky.ratorm.serializers.basic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import pl.szczurowsky.ratorm.serializers.Serializer;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.fail;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EnumSerializerTest {

    protected final HashMap<Class<?>, Class<? extends Serializer<?>>> serializers = new HashMap<>();

    enum TestEnum {
        A, B
    }

    /**
     * Register serializers
     */
    @BeforeAll
    public void setupSerializers() {
        this.serializers.put(Enum.class, EnumSerializer.class);

    }

    @Test
    public void TestEnumSerialization() {
        EnumSerializer enumSerializer = new EnumSerializer();
        try {
            Assertions.assertEquals(TestEnum.A, enumSerializer.deserialize(enumSerializer.serialize(TestEnum.A)));
            Assertions.assertEquals(TestEnum.B, enumSerializer.deserialize(enumSerializer.serialize(TestEnum.B)));
            Assertions.assertNotEquals(TestEnum.A, enumSerializer.deserialize(enumSerializer.serialize(TestEnum.B)));
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

}