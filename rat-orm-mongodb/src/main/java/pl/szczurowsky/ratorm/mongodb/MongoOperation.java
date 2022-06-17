package pl.szczurowsky.ratorm.mongodb;

import com.mongodb.client.ClientSession;
import pl.szczurowsky.ratorm.Database;
import pl.szczurowsky.ratorm.exceptions.OperationException;
import pl.szczurowsky.ratorm.operation.Operation;


public abstract class MongoOperation extends Operation {

    protected ClientSession clientSession;

    /**
     * Initialize runnable with database to execute query on
     *
     * @param database Database to execute query on
     */
    public MongoOperation(Database database) throws OperationException {
        super(database);
        if (!database.getClass().isAssignableFrom(MongoDB.class))
            throw new OperationException("Passed driver " + database.getClass().getName() + " to MongoDB type operation!");
        MongoDB mongoDB = (MongoDB) database;
        clientSession =  mongoDB.startSession();
    }

    @Override
    public void run() {
        try {
            if (this.useTransactions) {
                options.put("MongoDB.session", clientSession);
                try {
                    clientSession.startTransaction();
                    execute(this.options);
                    clientSession.commitTransaction();
                } catch (Exception e) {
                    e.printStackTrace();
                    clientSession.abortTransaction();
                } finally {
                    clientSession.close();
                }
            }
            else
                execute(this.options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
