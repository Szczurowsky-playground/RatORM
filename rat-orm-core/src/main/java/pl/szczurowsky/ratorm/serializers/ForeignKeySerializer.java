package pl.szczurowsky.ratorm.serializers;

import pl.szczurowsky.ratorm.Model.BaseModel;
import pl.szczurowsky.ratorm.annotation.ModelField;
import pl.szczurowsky.ratorm.database.Database;
import pl.szczurowsky.ratorm.exception.NoSerializerFoundException;
import pl.szczurowsky.ratorm.exception.SerializerException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

public class ForeignKeySerializer implements Serializer<Object> {

    public <T extends BaseModel> String serializeForeignKey(Class<T> foreignKeyClass, T foreignKeyValue, HashMap<Class<?>, Class<? extends Serializer>> serializers) throws SerializerException {
        try {
            String keyFieldName = "";
            String keyFieldValue = "";
            for (Field field : foreignKeyClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(ModelField.class) && field.getAnnotation(ModelField.class).isPrimaryKey()) {
                    field.setAccessible(true);
                    keyFieldName = field.getName();
                    keyFieldValue = serializeValue(field.getType(), serializers, field.get(foreignKeyValue));
                }
            }
            return keyFieldName + "#__#" + keyFieldValue;
        } catch (Exception e) {
            throw new SerializerException(e);
        }
    }

    public <T extends BaseModel> T deserializeForeignKey(Class<T> typeClass,String value, Database database) throws SerializerException {
        String key = value.split("#__#")[0];
        String valueToDeserialize = value.split("#__#")[1];
        try {
            return database.fetchMatching(typeClass, key, valueToDeserialize).get(0);
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
    public Object deserialize(String receivedObject) throws ClassNotFoundException {
        return null;
    }
}
