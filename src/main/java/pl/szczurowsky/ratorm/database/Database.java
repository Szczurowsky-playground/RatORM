package pl.szczurowsky.ratorm.database;

import pl.szczurowsky.ratorm.exceptions.AlreadyConnectedException;
import pl.szczurowsky.ratorm.exceptions.DatabaseDoesNotExistsException;
import pl.szczurowsky.ratorm.exceptions.NotConnectedToDatabaseException;

import java.util.Collection;
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
     * Choose used database
     * @param databases list of database
     */
    void connectToDatabase(Collection<String> databases) throws NotConnectedToDatabaseException, DatabaseDoesNotExistsException;

    /**
    Checks is connection to database valid
     */
    boolean isConnectionValid();

    /**
    Terminates connections
     */
    void terminateConnection() throws NotConnectedToDatabaseException;

}
