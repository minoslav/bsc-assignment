package sk.jancar.bsc.model;

/**
 * A business logic exception.
 * Should be thrown if some logical constraint of the application is violated.
 */
public class PTLogicException extends Exception {
    public PTLogicException() {
    }

    public PTLogicException(String message) {
        super(message);
    }

    public PTLogicException(String message, Throwable cause) {
        super(message, cause);
    }
}
