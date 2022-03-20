package pl.szczurowsky.ratorm.exception;

import java.util.Arrays;

public class DeserializerException extends Exception {
    public DeserializerException(Exception e) {
        super("Cannot deserialize object" + "Exception:\n" + Arrays.toString(e.getStackTrace()));
    }
}
