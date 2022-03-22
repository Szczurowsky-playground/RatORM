package pl.szczurowsky.ratorm.exception;

public class MoreThanOnePrimaryKeyException extends Exception {
    public MoreThanOnePrimaryKeyException() {
        super("Model have more than one fields set as primary key");
    }
}
