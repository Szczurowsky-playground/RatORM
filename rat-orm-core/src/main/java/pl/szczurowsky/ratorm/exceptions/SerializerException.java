package pl.szczurowsky.ratorm.exceptions;

import java.util.Arrays;

public class SerializerException extends Exception {
    public SerializerException(Exception e, Class<?> classType) {
        super("Cannot serialize object which is typeof" + classType.getName() + "\nException:\n" + Arrays.toString(e.getStackTrace()));
    }
    public SerializerException(String message) {
        super(message);
    }
}
