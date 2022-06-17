package pl.szczurowsky.ratorm.exceptions;

public class NoSerializerFoundException extends Exception {
    public NoSerializerFoundException() {
        super("There's no registered serializer for this class");
    }
}