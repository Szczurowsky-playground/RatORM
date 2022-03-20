package pl.szczurowsky.ratorm.exception;

public class AlreadyConnectedException extends Exception {
    public AlreadyConnectedException() {
        super("Trying to initiate connection which already exists");
    }
}
