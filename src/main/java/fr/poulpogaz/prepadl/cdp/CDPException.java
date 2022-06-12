package fr.poulpogaz.prepadl.cdp;

import fr.poulpogaz.prepadl.PrepaDLException;

public class CDPException extends PrepaDLException {

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
