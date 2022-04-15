package pl.szczurowsky.ratorm.exception;

public class PassedWrongDriverToOperationException extends Exception{
    public PassedWrongDriverToOperationException(String message) {
        super(message);
    }
}
