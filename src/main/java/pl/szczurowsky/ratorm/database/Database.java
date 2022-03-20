package pl.szczurowsky.ratorm.database;

import pl.szczurowsky.ratorm.enums.FilterExpression;
import pl.szczurowsky.ratorm.exception.*;
import pl.szczurowsky.ratorm.serializers.Serializer;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
    Basic interface of database which would be initiated at the begging.
    Contains all methods and fields required to work
 */
public interface Database {

    /**
        Connect to database via connection url
        @param uri URI string
        @throws AlreadyConnectedException Already connected to database
     */
    void connect(String uri) throws AlreadyConnectedException;

    /**
        Connect to database via credentials
        @param credentials Map with defined keys and values as credentials
        @throws AlreadyConnectedException Already connected to database
    */
    void connect(Map<String, String> credentials) throws AlreadyConnectedException;

    /**
     * Register new object serializer
     * @param serializedObjectClass Class of object which is going to be used with provided serializer
     * @param serializerClass Class of serializer
     */
    void registerSerializer(Class<?> serializedObjectClass, Class<? extends Serializer> serializerClass);

    /**
     * Initialize table in database. If table not existing in database than creating it. If exists than load
     * @param modelClass Class od model
     * @throws ModelAnnotationMissingException Exception when model is not using @Model annotation
     */
    void initModel(Class<?> modelClass) throws ModelAnnotationMissingException;

    /**
     * Fetch all objects which matches model class
     * @param modelClass Model class
     * @throws ModelAnnotationMissingException Exception when model is not using @Model annotation
     * @throws NotConnectedToDatabaseException Not connected to database
     * @throws ModelNotInitializedException Model wasn't initialized
     * @throws NoSerializerFoundException Serializer for field model wasn't found
     * @throws InvocationTargetException Java exception when wasn't able to invoke method
     * @throws InstantiationException Java exception when model class wasn't able to create own instance
     * @throws IllegalAccessException Java security exception
     */
    void fetchAll(Class<?> modelClass) throws ModelAnnotationMissingException, NotConnectedToDatabaseException, ModelNotInitializedException, NoSerializerFoundException, InvocationTargetException, InstantiationException, IllegalAccessException;

    /**
     * Fetch all objects which match provided conditions
     * @param modelClass Model class
     * @param key Object key in database (field name)
     * @param value Not serialized value of field
     * @param <T> Model class
     * @throws ModelAnnotationMissingException Exception when model is not using @Model annotation
     * @throws NotConnectedToDatabaseException Not connected to database
     * @throws ModelNotInitializedException Model wasn't initialized
     * @throws NoSerializerFoundException Serializer for field model wasn't found
     * @throws InvocationTargetException Java exception when wasn't able to invoke method
     * @throws InstantiationException Java exception when model class wasn't able to create own instance
     * @throws IllegalAccessException Java security exception
     */
    <T> void fetchMatching(Class<T> modelClass, String key, Object value) throws NotConnectedToDatabaseException, ModelNotInitializedException, ModelAnnotationMissingException, NoSerializerFoundException, InvocationTargetException, InstantiationException, IllegalAccessException;

    /**
     * Save object
     * @param object object
     * @param modelClass class of object model
     * @throws NoSerializerFoundException Serializer for field model wasn't found
     * @throws InstantiationException Java exception when model class wasn't able to create own instance
     * @throws IllegalAccessException Java security exception
     * @throws InvocationTargetException Java exception when wasn't able to invoke method
     * @throws NotConnectedToDatabaseException Not connected to database
     */
    void save(Object object, Class<?> modelClass) throws NoSerializerFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NotConnectedToDatabaseException;

    /**
     * Returns all object which match Model
     * @param modelClass Model to match
     * @return List of objects
     */
    <T> List<T> readAll(Class<T> modelClass);

    /**
     * Returns all object which match
     * @param modelClass class of object model
     * @param field matching field
     * @param expression expression from enum
     * @param value matched value to field
     * @return Array of objects which matched expression
     */
    <T> List<T> filter(Class<T> modelClass, String field, FilterExpression expression, Object value);

    /**
    Checks is connection to database valid
     */
    boolean isConnectionValid();

    /**
    Terminates connections
     * @throws NotConnectedToDatabaseException Not connected to database
     */
    void terminateConnection() throws NotConnectedToDatabaseException;

}
