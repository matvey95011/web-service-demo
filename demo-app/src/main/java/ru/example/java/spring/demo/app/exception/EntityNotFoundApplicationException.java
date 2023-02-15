package ru.example.java.spring.demo.app.exception;

public class EntityNotFoundApplicationException extends ApplicationException {

    public EntityNotFoundApplicationException() {
    }

    public EntityNotFoundApplicationException(String message) {
        super(message);
    }

    public EntityNotFoundApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityNotFoundApplicationException(Throwable cause) {
        super(cause);
    }

}
