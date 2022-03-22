package pl.szczurowsky.ratorm.serializers;

import org.json.JSONArray;
import pl.szczurowsky.ratorm.exception.SerializerException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class CollectionSerializer implements Serializer<Object> {

    public <T> String serializeCollection(Collection<T> providedCollection, HashMap<Class<?>, Class<? extends Serializer>> serializers) throws SerializerException {
        try {
            JSONArray serializedToJsonArray = new JSONArray();
            Class<?> valueClass = null;
            for (T t : providedCollection) {
                if (valueClass == null)
                    valueClass = t.getClass();
                serializedToJsonArray.put(serializeValue(t.getClass(), serializers, t));
            }
            if (valueClass == null)
                valueClass = Object.class;
            serializedToJsonArray.put(String.valueOf(valueClass).replace("class ", ""));
            return serializedToJsonArray.toString();
        } catch (Exception e) {
            throw new SerializerException(e);
        }
    }
    
    public <T> Collection<T> deserializeCollection(String receivedCollection, HashMap<Class<?>, Class<? extends Serializer>> serializers) throws ClassNotFoundException {
        JSONArray receivedArray = new JSONArray(receivedCollection);
        Collection<T> deserializedCollection = new ArrayList<>();
        Class<?> valueClass = Class.forName((String) receivedArray.get(receivedArray.length() - 1));
        receivedArray.remove(receivedArray.length() - 1);
        for (Object o : receivedArray) {
            deserializedCollection.add((T) deserializeValue(valueClass, serializers, o));
        }
        return deserializedCollection;
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
