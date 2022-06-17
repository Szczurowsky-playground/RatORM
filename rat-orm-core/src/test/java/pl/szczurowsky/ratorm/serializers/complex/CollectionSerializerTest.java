package pl.szczurowsky.ratorm.serializers.complex;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import pl.szczurowsky.ratorm.serializers.ComplexSerializer;
import pl.szczurowsky.ratorm.serializers.Serializer;
import pl.szczurowsky.ratorm.serializers.basic.StringSerializer;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.fail;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CollectionSerializerTest {

    protected final HashMap<Class<?>, Class<? extends Serializer<?>>> serializers = new HashMap<>();

    protected final HashMap<Class<?>, Class<? extends ComplexSerializer>> complexSerializers = new HashMap<>();

    /**
     * Register serializers
     */
    @BeforeAll
    public void setupSerializers() {
        this.serializers.put(String.class, StringSerializer.class);

        this.complexSerializers.put(AbstractList.class, CollectionSerializer.class);
    }

    /**
     * Test serializing of collection with basic types
     * and complex types
     */
    @Test
    public void TestCollectionSerializer() {
        CollectionSerializer collectionSerializer = new CollectionSerializer();
        Collection<String> collection = new ArrayList<>();
        collection.add("test");
        Collection<Collection<String>> collection1 = new ArrayList<>();
        collection1.add(collection);
        try {
            Assertions.assertEquals(collection, collectionSerializer.deserialize(collectionSerializer.serialize(collection, serializers, complexSerializers), serializers, complexSerializers));
            Assertions.assertEquals(collection1, collectionSerializer.deserialize(collectionSerializer.serialize(collection1, serializers, complexSerializers), serializers, complexSerializers));
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

}