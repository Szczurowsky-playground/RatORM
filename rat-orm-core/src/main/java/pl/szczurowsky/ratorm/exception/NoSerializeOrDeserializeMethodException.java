package pl.szczurowsky.ratorm.exception;

public class NoSerializeOrDeserializeMethodException extends Exception {
    public NoSerializeOrDeserializeMethodException() {
        super("Method deserialize/serialize wasn't found in serializer");
    }
}
