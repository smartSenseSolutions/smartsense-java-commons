package com.smartsensesolutions.commons.dao.exception;

public class BadOperatorException extends RuntimeException {

    public BadOperatorException() {
        super();
    }

    public BadOperatorException(String message) {
        super(message);
    }

    public BadOperatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadOperatorException(Throwable cause) {
        super(cause);
    }
}
