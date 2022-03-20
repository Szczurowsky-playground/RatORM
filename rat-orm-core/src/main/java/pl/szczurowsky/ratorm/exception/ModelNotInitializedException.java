package pl.szczurowsky.ratorm.exception;

public class ModelNotInitializedException extends Exception {
    public ModelNotInitializedException() {
        super("Model is not initalized");
    }
}
