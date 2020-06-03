package com.piedel.piotr.configuration.service.exceptions;

public class IncorrectEtagException extends Exception {

    public IncorrectEtagException(String message) {
        super(message);
    }

    public IncorrectEtagException(String message, Throwable cause) {
        super(message, cause);
    }
}
