package pl.szczurowsky.ratorm.serializers;

import java.math.BigInteger;

public class BigIntSerializer implements Serializer<BigInteger> {

    @Override
    public String serialize(Object providedObject) {
        return String.valueOf(providedObject);
    }

    @Override
    public BigInteger deserialize(String receivedBigInt) {
        return BigInteger.valueOf(Long.parseLong(receivedBigInt));
    }
}
