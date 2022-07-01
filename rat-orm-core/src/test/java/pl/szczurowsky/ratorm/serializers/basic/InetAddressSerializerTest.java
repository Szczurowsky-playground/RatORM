package pl.szczurowsky.ratorm.serializers.basic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import pl.szczurowsky.ratorm.serializers.Serializer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.fail;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InetAddressSerializerTest {

    protected final HashMap<Class<?>, Class<? extends Serializer<?>>> serializers = new HashMap<>();

    /**
     * Register serializers
     */
    @BeforeAll
    public void setupSerializers() {
        this.serializers.put(Enum.class, EnumSerializer.class);

    }

    @Test
    public void TestInetAddressSerializer() throws UnknownHostException {
        InetAddress address = InetAddress.getByName("127.0.0.1");
        InetAddressSerializer inetAddressSerializer = new InetAddressSerializer();
        try {
            Assertions.assertEquals(address, inetAddressSerializer.deserialize(inetAddressSerializer.serialize(address)));
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

}