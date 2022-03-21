package pl.szczurowsky.ratorm.serializers;

import org.json.JSONObject;
import pl.szczurowsky.ratorm.exception.SerializerException;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MapSerializer implements Serializer<Object> {

    public <K, V> Map<K, V> deserializeMap(String receivedMap, HashMap<Class<?>, Class<? extends Serializer>> serializers) throws ClassNotFoundException {
        JSONObject JSONObject = new JSONObject(receivedMap);
        Map<K, V> map = new HashMap<>();
        Class<?> keyClass = Class.forName(JSONObject.getString("$#MapKey#$"));
        Class<?> varClass = Class.forName(JSONObject.getString("$#MapVar#$"));
        JSONObject.remove("$#MapKey#$");
        JSONObject.remove("$#MapVar#$");
        for (String s : JSONObject.keySet()) {
            map.put((K) deserializeValue(keyClass, serializers, s), (V) deserializeValue(varClass, serializers, JSONObject.get(s)));
        }
        return map;
    }

    public <K, V> String serializeMap(Map<K, V> providedObject, HashMap<Class<?>, Class<? extends Serializer>> serializers) throws SerializerException {
        try {
            JSONObject jsonObject = new JSONObject();
            Class<?> KeyClass = null;
            Class<?> VarClass = null;
            for (K k : providedObject.keySet()) {
                KeyClass = k.getClass();
                V value = providedObject.get(k);
                VarClass = value.getClass();
                jsonObject.put(serializeValue(k.getClass(), serializers, k), serializeValue(value.getClass(), serializers, value));
            }
            jsonObject.put("$#MapKey#$", String.valueOf(KeyClass).replace("class ", ""));
            jsonObject.put("$#MapVar#$", String.valueOf(VarClass).replace("class ", ""));
            return jsonObject.toString();
        } catch (Exception e) {
            throw new SerializerException(e);
        }
    }

    public <T> Object deserializeValue(Class<T> modelClass, HashMap<Class<?>, Class<? extends Serializer>> serializers, Object object) {
        try {
            Class<? extends Serializer> serializer = serializers.get(modelClass);
            if (serializer != null) {
                for (Method declaredMethod : serializer.getDeclaredMethods()) {
                    if (declaredMethod.getName().equals("deserialize")) {
                        return (T) declaredMethod.invoke(serializer.newInstance(), object);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "CantDeserialize";
        }
        return "CantDeserialize";
    }

    public <T> String serializeValue(Class<T> modelClass, HashMap<Class<?>, Class<? extends Serializer>> serializers, Object object) {
        try {
            Class<? extends Serializer> serializer = serializers.get(modelClass);
            if (serializer != null) {
                for (Method declaredMethod : serializer.getDeclaredMethods()) {
                    if (declaredMethod.getName().equals("serialize")) {
                        return (String) declaredMethod.invoke(serializer.newInstance(), object);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "CantSerialize";
        }
        return "CantSerialize";
    }

    @Override
    public String serialize(Object providedObject) throws SerializerException {
        return null;
    }

    @Override
    public Object deserialize(String receivedObject) {
        return null;
    }
}
