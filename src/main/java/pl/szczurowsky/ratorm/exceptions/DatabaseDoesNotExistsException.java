package pl.szczurowsky.ratorm.exceptions;

public class DatabaseDoesNotExistsException extends Exception {
    public DatabaseDoesNotExistsException() {
        super("Provided database name does not exist");
    }
}
