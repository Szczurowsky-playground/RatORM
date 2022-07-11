package pl.szczurowsky.ratorm.serializers.complex;

import org.json.JSONArray;
import pl.szczurowsky.ratorm.exceptions.SerializerException;
import pl.szczurowsky.ratorm.serializers.ComplexSerializer;
import pl.szczurowsky.ratorm.serializers.Serializer;
import pl.szczurowsky.ratorm.util.SerializerUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Helper built-in serializer for processing Collection
 * @see Collection
 * @see ComplexSerializer
 * @param <T> Type of collection elements
 */
public class CollectionSerializer<T> extends ComplexSerializer<Collection<T>> {

    @Override
    public String serialize(Collection<T> providedObject, HashMap<Class<?>, Class<? extends Serializer<?>>> serializers, HashMap<Class<?>, Class<? extends ComplexSerializer>> complexSerializers) throws SerializerException {
        try {
            JSONArray jsonArray = new JSONArray();
            Class<T> classType = null;
            for (T object : providedObject) {
                if (classType == null)
                    classType = (Class<T>) object.getClass();
                jsonArray.put(SerializerUtil.serializeValue(classType, object, serializers, complexSerializers));
            }
            if (classType == null)
                throw new SerializerException("No class type found");
            jsonArray.put(String.valueOf(classType).replace("class ", ""));
            return jsonArray.toString();
        } catch (Exception e) {
            throw new SerializerException(e);
        }
    }

    @Override
    public Collection<T> deserialize(String receivedObject, HashMap<Class<?>, Class<? extends Serializer<?>>> serializers, HashMap<Class<?>, Class<? extends ComplexSerializer>> complexSerializers) throws ClassNotFoundException, SerializerException {
        try {
            JSONArray jsonArray = new JSONArray(receivedObject);
            Collection<T> collection = new ArrayList<>();
            Class<T> classType = (Class<T>) Class.forName(jsonArray.getString(jsonArray.length() - 1));
            jsonArray.remove(jsonArray.length() - 1);
            for (Object t : jsonArray) {
                collection.add((T) SerializerUtil.deserializeValue(classType, t.toString(), serializers, complexSerializers));
            }
            return collection;
        } catch (Exception e) {
            throw new SerializerException(e);
        }
    }
}
