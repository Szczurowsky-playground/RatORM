package pl.szczurowsky.ratorm.exceptions;

public class AlreadyConnectedException extends Exception {
    public AlreadyConnectedException() {
        super("Trying to initiate connection which already exists");
    }
}
