package pl.szczurowsky.ratorm.exceptions;

public class NotConnectedToDatabaseException extends Exception {

    public NotConnectedToDatabaseException() {
        super("There's no connection to database");
    }

}
