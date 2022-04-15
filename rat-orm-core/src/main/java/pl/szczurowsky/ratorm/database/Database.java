package pl.szczurowsky.ratorm.database;

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
    <T extends BaseModel> void initModel(Collection<Class<T>> modelClasses) throws ModelAnnotationMissingException, MoreThanOnePrimaryKeyException, NoPrimaryKeyException;

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
     * Read all objects from cache
     * @param modelClass Model class
     * @param <T> Model class
     * @return List of models
     * @throws NotCachedException Exception when user tries to read object from cache which don't have cache enabled
     */
    <T extends BaseModel> List<T> readAllFromCache(Class<T> modelClass) throws NotCachedException;

    /**
     * Read objects which match condition
     * @param modelClass Model class
     * @param field Matching field
     * @param value matched value to field
     * @param <T> Model class
     * @return List of models
     * @throws NotCachedException Exception when user tries to read object from cache which don't have cache enabled
     */
    <T extends BaseModel> List<T> readMatchingFromCache(Class<T> modelClass, String field, Object value) throws NotCachedException;

    /**
     * Refresh whole cache - purging cache and replace it with object from database
     * @param object object
     * @param modelClass Model class
     * @throws NotCachedException Exception when user tries to read object from cache which don't have cache enabled
     * @throws ModelAnnotationMissingException Exception when model is not using @Model annotation
     * @throws NotConnectedToDatabaseException Not connected to database
     * @throws ModelNotInitializedException Model wasn't initialized
     * @throws NoSerializerFoundException Serializer for field model wasn't found
     * @throws InvocationTargetException Java exception when wasn't able to invoke method
     * @throws InstantiationException Java exception when model class wasn't able to create own instance
     * @throws IllegalAccessException Java security exception
     */
    void updateWholeCache(Object object, Class<? extends BaseModel> modelClass) throws NotCachedException, NoSerializerFoundException, NotConnectedToDatabaseException, ModelNotInitializedException, InvocationTargetException, ModelAnnotationMissingException, InstantiationException, IllegalAccessException;

    /**
     *
     * @param modelClass Model class
     * @param key Object key in database (field name)
     * @param value Not serialized value of field
     * @param <T> Model class
     * @throws NotCachedException Exception when user tries to read object from cache which don't have cache enabled
     * @throws ModelAnnotationMissingException Exception when model is not using @Model annotation
     * @throws NotConnectedToDatabaseException Not connected to database
     * @throws ModelNotInitializedException Model wasn't initialized
     * @throws NoSerializerFoundException Serializer for field model wasn't found
     * @throws InvocationTargetException Java exception when wasn't able to invoke method
     * @throws InstantiationException Java exception when model class wasn't able to create own instance
     * @throws IllegalAccessException Java security exception
     */
    <T extends BaseModel> void updateMatchingCache(Class<T> modelClass, String key, Object value) throws NotCachedException, NoSerializerFoundException, NotConnectedToDatabaseException, ModelNotInitializedException, InvocationTargetException, ModelAnnotationMissingException, InstantiationException, IllegalAccessException;

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
