package operation;

import pl.szczurowsky.ratorm.database.Database;

import java.util.HashMap;

public abstract class BaseOperation implements OperationRunnable {

    private int id;

    protected boolean useTransactions = false;

    protected HashMap<String, Object> options = new HashMap<>();

    private Database database;

    public BaseOperation(Database database) {
        this.database = database;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
