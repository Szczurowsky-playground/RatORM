package pl.szczurowsky.ratorm.exception;

public class NoPrimaryKeyException extends Exception {
    public NoPrimaryKeyException() {
        super("Model don't have definied primary key field");
    }
}
