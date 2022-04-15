package pl.szczurowsky.ratorm.database;

import operation.OperationManager;
import pl.szczurowsky.ratorm.Model.BaseModel;
import pl.szczurowsky.ratorm.enums.FilterExpression;
import pl.szczurowsky.ratorm.exception.*;
import pl.szczurowsky.ratorm.serializers.Serializer;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
    Basic interface of database which would be initiated at the begging.
    Contains all methods and fields required to work
 */
public interface Database {

    /**
     * Returns manager of operations
     * @return OperationManager
     */
    OperationManager getOperationManager();

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
     * @param modelClasses One or multiple class of models
     * @throws ModelAnnotationMissingException Exception when model is not using @Model annotation
     * @throws MoreThanOnePrimaryKeyException Exception when model have more than one field set as primary key
     * @throws NoPrimaryKeyException Exception when model don't have field as primary key
     */
    void initModel(Collection<Class<? extends BaseModel>> modelClasses) throws ModelAnnotationMissingException, MoreThanOnePrimaryKeyException, NoPrimaryKeyException;

    /**
     * Fetch all objects which matches model class
     * @param <T> Model class
     * @param modelClass Model class
     * @throws ModelAnnotationMissingException Exception when model is not using @Model annotation
     * @throws NotConnectedToDatabaseException Not connected to database
     * @throws ModelNotInitializedException Model wasn't initialized
     * @throws NoSerializerFoundException Serializer for field model wasn't found
     * @throws InvocationTargetException Java exception when wasn't able to invoke method
     * @throws InstantiationException Java exception when model class wasn't able to create own instance
     * @throws IllegalAccessException Java security exception
     * @return List of fetched objects
     */
    <T extends BaseModel> List<T> fetchAll(Class<T> modelClass) throws ModelAnnotationMissingException, NotConnectedToDatabaseException, ModelNotInitializedException, NoSerializerFoundException, InvocationTargetException, InstantiationException, IllegalAccessException;

    /**
     * Fetch all objects which match provided conditions
     * @param <T> Model class
     * @param modelClass Model class
     * @param key Object key in database (field name)
     * @param value Not serialized value of field
     * @throws ModelAnnotationMissingException Exception when model is not using @Model annotation
     * @throws NotConnectedToDatabaseException Not connected to database
     * @throws ModelNotInitializedException Model wasn't initialized
     * @throws NoSerializerFoundException Serializer for field model wasn't found
     * @throws InvocationTargetException Java exception when wasn't able to invoke method
     * @throws InstantiationException Java exception when model class wasn't able to create own instance
     * @throws IllegalAccessException Java security exception
     * @return List of fetched objects
     */
    <T extends BaseModel> List<T> fetchMatching(Class<T> modelClass, String key, Object value) throws NotConnectedToDatabaseException, ModelNotInitializedException, ModelAnnotationMissingException, NoSerializerFoundException, InvocationTargetException, InstantiationException, IllegalAccessException;

    /**
     * Save Many object
     * @param <T> Model class
     * @param objects Collection of objects
     * @param modelClass class of object model
     * @throws NoSerializerFoundException Serializer for field model wasn't found
     * @throws InstantiationException Java exception when model class wasn't able to create own instance
     * @throws IllegalAccessException Java security exception
     * @throws InvocationTargetException Java exception when wasn't able to invoke method
     * @throws NotConnectedToDatabaseException Not connected to database
     */
    <T extends BaseModel> void saveMany(Collection<T> objects, Class<T> modelClass) throws NoSerializerFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NotConnectedToDatabaseException;

    /**
     * Save Many object
     * @param <T> Model class
     * @param objects Collection of objects
     * @param modelClass class of object model
     * @param options Options for saving
     * @throws NoSerializerFoundException Serializer for field model wasn't found
     * @throws InstantiationException Java exception when model class wasn't able to create own instance
     * @throws IllegalAccessException Java security exception
     * @throws InvocationTargetException Java exception when wasn't able to invoke method
     * @throws NotConnectedToDatabaseException Not connected to database
     */
    <T extends BaseModel> void saveMany(Collection<T> objects, Class<T> modelClass, Map<String, Object> options) throws NoSerializerFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NotConnectedToDatabaseException;


    /**
     * Save object
     * @param <T> Model class
     * @param object object
     * @param modelClass class of object model
     * @throws NoSerializerFoundException Serializer for field model wasn't found
     * @throws InstantiationException Java exception when model class wasn't able to create own instance
     * @throws IllegalAccessException Java security exception
     * @throws InvocationTargetException Java exception when wasn't able to invoke method
     * @throws NotConnectedToDatabaseException Not connected to database
     */
    <T extends BaseModel> void save(T object, Class<T> modelClass) throws NoSerializerFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NotConnectedToDatabaseException;

    /**
     * Save object
     * @param <T> Model class
     * @param object object
     * @param modelClass class of object model
     * @param options Options for saving
     * @throws NoSerializerFoundException Serializer for field model wasn't found
     * @throws InstantiationException Java exception when model class wasn't able to create own instance
     * @throws IllegalAccessException Java security exception
     * @throws InvocationTargetException Java exception when wasn't able to invoke method
     * @throws NotConnectedToDatabaseException Not connected to database
     */
    <T extends BaseModel> void save(T object, Class<T> modelClass, Map<String, Object> options) throws NoSerializerFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NotConnectedToDatabaseException;

    /**
     * Returns all object which match
     * @param <T> Model class
     * @param modelClass class of object model
     * @param field matching field
     * @param expression expression from enum
     * @param value matched value to field
     * @param objects provided stream of objects
     * @return Array of objects which matched expression
     */
    <T extends BaseModel> List<T> filter(Class<T> modelClass, String field, FilterExpression expression, Object value, Stream<T> objects);

    /**
     * Deletes object in database which matches provided object
     * @param <T> Model class
     * @param modelClass Model class
     * @param object object
     * @throws NotConnectedToDatabaseException Not connected to database
     * @throws NoSerializerFoundException Serializer for field model wasn't found
     * @throws InstantiationException Java exception when model class wasn't able to create own instance
     * @throws IllegalAccessException Java security exception
     * @throws InvocationTargetException Java exception when wasn't able to invoke method
     */
    <T extends BaseModel> void delete(T object, Class<T> modelClass) throws NotConnectedToDatabaseException, NoSerializerFoundException, InstantiationException, IllegalAccessException, InvocationTargetException;

    /**
     * Checks is connection to database valid
     * @return boolean
     */
    boolean isConnectionValid();

    /**
    Terminates connections
     * @throws NotConnectedToDatabaseException Not connected to database
     */
    void terminateConnection() throws NotConnectedToDatabaseException;

}
