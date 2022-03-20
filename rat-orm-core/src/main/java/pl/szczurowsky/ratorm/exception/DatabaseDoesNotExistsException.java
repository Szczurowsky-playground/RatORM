package pl.szczurowsky.ratorm.exception;

public class DatabaseDoesNotExistsException extends Exception {
    public DatabaseDoesNotExistsException() {
        super("Provided database name does not exist");
    }
}
