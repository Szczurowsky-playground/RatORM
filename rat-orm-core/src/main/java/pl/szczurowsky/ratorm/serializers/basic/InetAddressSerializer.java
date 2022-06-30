package pl.szczurowsky.ratorm.serializers.basic;

import pl.szczurowsky.ratorm.exceptions.SerializerException;
import pl.szczurowsky.ratorm.serializers.Serializer;

import java.net.InetAddress;

public class InetAddressSerializer implements Serializer<InetAddress> {
    @Override
    public String serialize(InetAddress inetAddress) throws SerializerException {
        return inetAddress.toString();
    }

    @Override
    public InetAddress deserialize(String s) throws ClassNotFoundException {
        try {
            return InetAddress.getByName(s);
        } catch (Exception e) {
            throw new ClassNotFoundException("InetAddressSerializer: " + e.getMessage());
        }
    }
}
