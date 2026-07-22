package com.invencore.app.exception;

public class PagoException extends BusinessException {

    public PagoException(String message) {
        super(message);
    }

    public PagoException(String message, Throwable cause) {
        super(message, cause);
    }
}
