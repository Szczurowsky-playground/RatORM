package pl.szczurowsky.ratorm.exception;

import java.util.Arrays;

public class SerializerException extends Exception {
    public SerializerException(Exception e) {
        super("Cannot serialize object" + "Exception:\n" + Arrays.toString(e.getStackTrace()));
    }
    public SerializerException(String message) {
        super(message);
    }
}
