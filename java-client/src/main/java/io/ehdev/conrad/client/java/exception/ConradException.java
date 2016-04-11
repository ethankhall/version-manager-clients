package io.ehdev.conrad.client.java.exception;

public class ConradException extends RuntimeException {

    public ConradException(String message) {
        this(message, null);
    }

    public ConradException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ConradException(Throwable throwable) {
        super(throwable);
    }
}
