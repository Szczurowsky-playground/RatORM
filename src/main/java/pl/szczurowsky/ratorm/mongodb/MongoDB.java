package pl.szczurowsky.ratorm.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import pl.szczurowsky.ratorm.database.Database;
import pl.szczurowsky.ratorm.exceptions.AlreadyConnectedException;
import pl.szczurowsky.ratorm.exceptions.DatabaseDoesNotExistsException;
import pl.szczurowsky.ratorm.exceptions.NotConnectedToDatabaseException;

import java.util.*;

public class MongoDB implements Database {

    private MongoClient client;
    private final List<MongoDatabase> databases = new LinkedList<>();
    private boolean connected;

    @Override
    public void connect(String uri) throws AlreadyConnectedException {
        if (connected)
            throw new AlreadyConnectedException();
        this.client = new MongoClient(new MongoClientURI(uri));
        this.connected = true;
    }

    @Override
    public void connect(Map<String, String> credentials) throws AlreadyConnectedException {
        if (connected)
            throw new AlreadyConnectedException();
        MongoCredential credential = MongoCredential.createCredential(
                credentials.get("username"),
                credentials.get("name"),
                credentials.get("password").toCharArray()
        );
        this.client = new MongoClient(new ServerAddress(credentials.get("host"), Integer.parseInt(credentials.get("port"))), Collections.singletonList(credential));
        this.connected = true;
    }

    @Override
    public void connectToDatabase(Collection<String> databases) throws NotConnectedToDatabaseException, DatabaseDoesNotExistsException {
        if (connected) {
            for (String database : databases) {
                if (!this.client.getDatabaseNames().contains(database))
                    throw new DatabaseDoesNotExistsException();
                this.databases.add(this.client.getDatabase(database));
            }
        }
        else
            throw new NotConnectedToDatabaseException();
    }

    @Override
    public boolean isConnectionValid() {
        try {
            this.client.getAddress();
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    @Override
    public void terminateConnection() throws NotConnectedToDatabaseException {
        if (connected) {
            this.client.close();
            this.connected = false;
        }
        else
            throw new NotConnectedToDatabaseException();
    }
}
