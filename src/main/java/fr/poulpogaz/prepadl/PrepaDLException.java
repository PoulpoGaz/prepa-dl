package fr.poulpogaz.prepadl;

public class PrepaDLException extends Exception {

    public PrepaDLException() {
    }

    public PrepaDLException(String message) {
        super(message);
    }

    public PrepaDLException(String message, Throwable cause) {
        super(message, cause);
    }

    public PrepaDLException(Throwable cause) {
        super(cause);
    }

    public PrepaDLException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
