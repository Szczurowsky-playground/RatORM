package pl.szczurowsky.ratorm.database;

import pl.szczurowsky.ratorm.enums.FilterExpression;
import pl.szczurowsky.ratorm.exception.AlreadyConnectedException;
import pl.szczurowsky.ratorm.exception.ModelAnnotationMissingException;
import pl.szczurowsky.ratorm.exception.NoSerializerFoundException;
import pl.szczurowsky.ratorm.exception.NotConnectedToDatabaseException;

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
     */
    void connect(String uri) throws AlreadyConnectedException;

    /**
        Connect to database via credentials
        @param credentials Map with defined keys and values as credentials
    */
    void connect(Map<String, String> credentials) throws AlreadyConnectedException;

    /**
     * Initialize table in database. If table not existing in database than creating it. If exists than load
     * @param modelClass Class od model
     * @throws ModelAnnotationMissingException Exception when model is not using @Model annotation
     */
    void initModel(Class<?> modelClass) throws ModelAnnotationMissingException;

    /**
     * Save object
     * @param object object
     * @param modelClass class of object model
     */
    void save(Object object, Class<?> modelClass) throws NoSerializerFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException;

    /**
     * Returns all object which match Model
     * @param modelClass Model to match
     * @return List of objects
     */
    public <T> List<T> readAll(Class<T> modelClass);

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
     */
    void terminateConnection() throws NotConnectedToDatabaseException;

}
