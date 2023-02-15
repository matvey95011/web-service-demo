package ru.example.java.spring.demo.app.exception;

public class IllegalArgumentApplicationException extends ApplicationException {

    public IllegalArgumentApplicationException() {
    }

    public IllegalArgumentApplicationException(String message) {
        super(message);
    }

    public IllegalArgumentApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalArgumentApplicationException(Throwable cause) {
        super(cause);
    }
}
