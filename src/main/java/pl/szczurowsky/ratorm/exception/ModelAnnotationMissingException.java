package pl.szczurowsky.ratorm.exception;

public class ModelAnnotationMissingException extends Exception {
    public ModelAnnotationMissingException() {
        super("Provided class is not using @Model annotation");
    }
}
