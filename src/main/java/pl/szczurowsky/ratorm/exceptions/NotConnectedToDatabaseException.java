package pl.szczurowsky.ratorm.exceptions;

public class NotConnectedToDatabaseException extends Exception {
    public NotConnectedToDatabaseException() {
        super("Trying to action which requires you to be connected to database");
    }
}
