package sk.jancar.bsc.model;

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
