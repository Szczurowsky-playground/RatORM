package pl.szczurowsky.ratorm.serializers;

import java.math.BigInteger;

public class BigIntSerializer implements Serializer {

    @Override
    public String serialize(Object providedObject) {
        return String.valueOf(providedObject);
    }

    public BigInteger deserialize(String receivedBigInt) {
        return BigInteger.valueOf(Long.parseLong(receivedBigInt));
    }
}
