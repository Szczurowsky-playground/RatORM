package pl.szczurowsky.ratorm.util;

import pl.szczurowsky.ratorm.exceptions.NoSerializerFoundException;
import pl.szczurowsky.ratorm.exceptions.SerializerException;
import pl.szczurowsky.ratorm.serializers.ComplexSerializer;
import pl.szczurowsky.ratorm.serializers.Serializer;

import java.lang.reflect.Method;
import java.util.HashMap;

public class SerializerUtil {

    /**
     * Deserialize provided string to D
     * @param modelClass Class of object to deserialize
     * @param object String to deserialize
     * @param serializers HashMap with registered serialized
     * @param complexSerializers HashMap with registered complex serializers
     * @return Object of type D
     * @param <D> Type of deserialized object
     * @throws SerializerException Wasn't able to deserialize object
     */
    public static  <D> D deserializeValue(Class<D> modelClass, String object, HashMap<Class<?>, Class<? extends Serializer<?>>> serializers, HashMap<Class<?>, Class<? extends ComplexSerializer>> complexSerializers) throws SerializerException {
        try {
            Class<? extends Serializer<?>> serializer = serializers.get(modelClass);
            Class<? extends ComplexSerializer> complexSerializer = complexSerializers.get(modelClass);
            if (serializer == null)
                serializer = serializers.get(modelClass.getSuperclass());
            if (serializer != null) {
                for (Method declaredMethod : serializer.getDeclaredMethods()) {
                    if (declaredMethod.getName().equals("deserialize")) {
                        return (D) declaredMethod.invoke(serializer.newInstance(), object);
                    }
                }
            }
            if (complexSerializer == null)
                complexSerializer = complexSerializers.get(modelClass.getSuperclass());
            if (complexSerializer != null) {
                for (Method declaredMethod : complexSerializer.getDeclaredMethods()) {
                    if (declaredMethod.getName().equals("deserialize")) {
                        return (D) declaredMethod.invoke(complexSerializer.newInstance(), object, serializers, complexSerializers);
                    }
                }
            }
            throw new NoSerializerFoundException();
        } catch (Exception e) {
            throw new SerializerException(e, modelClass);
        }
    }

    /**
     * Serialize provided object to string
     * @param modelClass Class of object to serialize
     * @param object Object to serialize
     * @param serializers HashMap with registered serialized
     * @param complexSerializers HashMap with registered complex serializers
     * @return String to store in database
     * @param <D> Type of serialized object
     * @throws SerializerException Wasn't able to serialize object
     */
    public static <D> String serializeValue(Class<D> modelClass, Object object, HashMap<Class<?>, Class<? extends Serializer<?>>> serializers, HashMap<Class<?>, Class<? extends ComplexSerializer>> complexSerializers) throws SerializerException {
        try {
            Class<? extends Serializer<?>> serializer = serializers.get(modelClass);
            Class<? extends ComplexSerializer> complexSerializer = complexSerializers.get(modelClass);
            if (serializer == null)
                serializer = serializers.get(modelClass.getSuperclass());
            if (serializer != null) {
                for (Method declaredMethod : serializer.getDeclaredMethods()) {
                    if (declaredMethod.getName().equals("serialize")) {
                        return (String) declaredMethod.invoke(serializer.newInstance(), object);
                    }
                }
            }
            if (complexSerializer == null)
                complexSerializer = complexSerializers.get(modelClass.getSuperclass());
            if (complexSerializer != null) {
                for (Method declaredMethod : complexSerializer.getDeclaredMethods()) {
                    if (declaredMethod.getName().equals("serialize")) {
                        return (String) declaredMethod.invoke(complexSerializer.newInstance(), object, serializers, complexSerializers);
                    }
                }
            }
            throw new NoSerializerFoundException();
        } catch (Exception e) {
            throw new SerializerException(e, modelClass);
        }
    }

}
