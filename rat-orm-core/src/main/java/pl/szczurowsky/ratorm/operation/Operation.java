package pl.szczurowsky.ratorm.operation;

import pl.szczurowsky.ratorm.Database;

import java.util.HashMap;

/**
 * Runnable for executing more complex database operations.
 */
public abstract class Operation implements OperationRunnable {

    /**
     * ID of runnable in manager
     */
    private int id;

    /**
     * Should transaction be used in query
     */
    protected boolean useTransactions = false;

    /**
     * Custom options for query
     */
    protected HashMap<String, Object> options = new HashMap<>();

    /**
     * Database to execute query on
     */
    private Database database;

    /**
     * Initialize runnable with database to execute query on
     * @param database Database to execute query on
     */
    public Operation(Database database) {
        this.database = database;
    }

    /**
     * Get ID of runnable in manager
     * @return ID of runnable
     */
    public int getId() {
        return id;
    }

    /**
     * Set ID of runnable in manager
     * @param id ID of runnable
     */
    public void setId(int id) {
        this.id = id;
    }

}