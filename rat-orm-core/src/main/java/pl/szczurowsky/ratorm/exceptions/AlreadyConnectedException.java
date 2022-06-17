package pl.szczurowsky.ratorm.exceptions;

public class AlreadyConnectedException extends Exception {

    public AlreadyConnectedException() {
        super("You're already connected to database");
    }
}
