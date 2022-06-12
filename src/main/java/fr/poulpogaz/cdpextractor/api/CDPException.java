package fr.poulpogaz.cdpextractor.api;

public class CDPException extends Exception {

    public CDPException() {
    }

    public CDPException(String message) {
        super(message);
    }

    public CDPException(String message, Throwable cause) {
        super(message, cause);
    }

    public CDPException(Throwable cause) {
        super(cause);
    }

    public CDPException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
