package pl.szczurowsky.ratorm.exception;

public class NotCachedException extends Exception {
    public NotCachedException() {
        super("Trying to read objects from cache which are not serialized");
    }
}
