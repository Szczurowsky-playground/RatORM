package pl.szczurowsky.ratorm.mongodb.operations;

import com.mongodb.client.ClientSession;
import operation.BaseOperation;
import pl.szczurowsky.ratorm.database.Database;
import pl.szczurowsky.ratorm.exception.PassedWrongDriverToOperationException;
import pl.szczurowsky.ratorm.mongodb.MongoDB;

public abstract class MongoOperation extends BaseOperation {

    ClientSession clientSession;

    public MongoOperation(Database database) throws PassedWrongDriverToOperationException {
        super(database);
        if (!database.getClass().isAssignableFrom(MongoDB.class))
            throw new PassedWrongDriverToOperationException("Passed driver " + database.getClass().getName() + " to MongoDB type operation!");
        MongoDB mongoDB = (MongoDB) database;
        clientSession =  mongoDB.getClientSession();
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
