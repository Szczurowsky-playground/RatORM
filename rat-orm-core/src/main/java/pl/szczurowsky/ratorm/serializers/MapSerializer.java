package pl.szczurowsky.ratorm.serializers;

import org.json.JSONObject;
import pl.szczurowsky.ratorm.exception.NoSerializerFoundException;
import pl.szczurowsky.ratorm.exception.SerializerException;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MapSerializer implements Serializer<Object> {

    public <K, V> Map<K, V> deserializeMap(String receivedMap, HashMap<Class<?>, Class<? extends Serializer>> serializers) throws SerializerException {
        try {
            JSONObject JSONObject = new JSONObject(receivedMap);
            Map<K, V> map = new HashMap<>();
            Class<?> keyClass = Class.forName(JSONObject.getString("$#MapKey#$"));
            Class<?> valueClass = Class.forName(JSONObject.getString("$#MapVar#$"));
            JSONObject.remove("$#MapKey#$");
            JSONObject.remove("$#MapVar#$");
            for (String s : JSONObject.keySet()) {
                map.put((K) deserializeValue(keyClass, serializers, s), (V) deserializeValue(valueClass, serializers, JSONObject.get(s)));
            }
            return map;
        }
        catch (Exception e) {
            throw new SerializerException(e);
        }
    }

    public <K, V> String serializeMap(Map<K, V> providedObject, HashMap<Class<?>, Class<? extends Serializer>> serializers) throws SerializerException {
        try {
            JSONObject jsonObject = new JSONObject();
            Class<?> keyClass = null;
            Class<?> valueClass = null;
            for (K k : providedObject.keySet()) {
                V value = providedObject.get(k);
                if (keyClass == null)
                    keyClass = k.getClass();
                if (valueClass == null)
                    valueClass = value.getClass();
                jsonObject.put(serializeValue(k.getClass(), serializers, k), serializeValue(value.getClass(), serializers, value));
            }
            if (keyClass == null || valueClass == null) {
                keyClass = Object.class;
                valueClass = Object.class;
            }
            jsonObject.put("$#MapKey#$", String.valueOf(keyClass).replace("class ", ""));
            jsonObject.put("$#MapVar#$", String.valueOf(valueClass).replace("class ", ""));
            return jsonObject.toString();
        } catch (Exception e) {
            throw new SerializerException(e);
        }
    }

    public <T> Object deserializeValue(Class<T> modelClass, HashMap<Class<?>, Class<? extends Serializer>> serializers, Object object) throws NoSerializerFoundException {
        try {
            Class<? extends Serializer> serializer = serializers.get(modelClass);
            if (serializer == null)
                serializer = serializers.get(modelClass.getSuperclass());
            if (serializer != null) {
                for (Method declaredMethod : serializer.getDeclaredMethods()) {
                    if (declaredMethod.getName().equals("deserialize")) {
                        return (T) declaredMethod.invoke(serializer.newInstance(), object);
                    }
                }
            }
        } catch (Exception e) {
            throw new NoSerializerFoundException();
        }
        throw new NoSerializerFoundException();
    }

    public <T> String serializeValue(Class<T> modelClass, HashMap<Class<?>, Class<? extends Serializer>> serializers, Object object) throws NoSerializerFoundException {
        try {
            Class<? extends Serializer> serializer = serializers.get(modelClass);
            if (serializer == null)
                serializer = serializers.get(modelClass.getSuperclass());
            if (serializer != null) {
                for (Method declaredMethod : serializer.getDeclaredMethods()) {
                    if (declaredMethod.getName().equals("serialize")) {
                        return (String) declaredMethod.invoke(serializer.newInstance(), object);
                    }
                }
            }
        } catch (Exception e) {
            throw new NoSerializerFoundException();
        }
        throw new NoSerializerFoundException();
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
