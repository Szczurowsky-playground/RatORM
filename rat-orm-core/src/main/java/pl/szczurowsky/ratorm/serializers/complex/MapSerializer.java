package pl.szczurowsky.ratorm.serializers.complex;

import org.json.JSONObject;
import pl.szczurowsky.ratorm.exceptions.SerializerException;
import pl.szczurowsky.ratorm.serializers.ComplexSerializer;
import pl.szczurowsky.ratorm.serializers.Serializer;
import pl.szczurowsky.ratorm.util.SerializerUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper built-in serializer for processing Map
 * @see Map
 * @see ComplexSerializer
 * @param <K> Key type
 * @param <V> Value type
 */
public class MapSerializer<K, V> extends ComplexSerializer<Map<K,V>> {


    @Override
    public String serialize(Map<K, V> providedObject, HashMap<Class<?>, Class<? extends Serializer<?>>> serializers, HashMap<Class<?>, Class<? extends ComplexSerializer>> complexSerializers) throws SerializerException {
        try {
            JSONObject jsonObject = new JSONObject();
            Class<K> keyClass = null;
            Class<V> valueClass = null;

            for (K key: providedObject.keySet()) {
                V value = providedObject.get(key);
                if (keyClass == null)
                    keyClass = (Class<K>) key.getClass();
                if (valueClass == null)
                    valueClass = (Class<V>) value.getClass();
                jsonObject.put(SerializerUtil.serializeValue(keyClass, key, serializers, complexSerializers), SerializerUtil.serializeValue(valueClass, value, serializers, complexSerializers));
            }
            jsonObject.put("$#MapKey#$", String.valueOf(keyClass).replace("class ", ""));
            jsonObject.put("$#MapVar#$", String.valueOf(valueClass).replace("class ", ""));
            return jsonObject.toString();
        } catch (Exception e) {
            throw new SerializerException(e);
        }
    }

    @Override
    public Map<K, V> deserialize(String receivedObject, HashMap<Class<?>, Class<? extends Serializer<?>>> serializers, HashMap<Class<?>, Class<? extends ComplexSerializer>> complexSerializers) throws ClassNotFoundException, SerializerException {
        try {
            JSONObject jsonObject = new JSONObject(receivedObject);
            Class<?> keyClass = Class.forName(jsonObject.getString("$#MapKey#$"));
            Class<?> valueClass = Class.forName(jsonObject.getString("$#MapVar#$"));
            HashMap<K, V> map = new HashMap<>();
            jsonObject.remove("$#MapKey#$");
            jsonObject.remove("$#MapVar#$");
            for (String s : jsonObject.keySet()) {
                map.put((K) SerializerUtil.deserializeValue(keyClass, s, serializers, complexSerializers), (V) SerializerUtil.deserializeValue(valueClass,jsonObject.getString(s), serializers, complexSerializers));
            }
            return map;
        } catch (Exception e) {
            throw new SerializerException(e);
        }
    }

}
