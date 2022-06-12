package fr.poulpogaz.prepadl.anthonylick;

import fr.poulpogaz.prepadl.PrepaDLException;

public class ALException extends PrepaDLException {

    public ALException() {
    }

    public ALException(String message) {
        super(message);
    }

    public ALException(String message, Throwable cause) {
        super(message, cause);
    }

    public ALException(Throwable cause) {
        super(cause);
    }

    public ALException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
