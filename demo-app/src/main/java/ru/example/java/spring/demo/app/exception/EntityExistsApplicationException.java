package ru.example.java.spring.demo.app.exception;

public class EntityExistsApplicationException extends ApplicationException {

    public EntityExistsApplicationException() {
    }

    public EntityExistsApplicationException(String message) {
        super(message);
    }

    public EntityExistsApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityExistsApplicationException(Throwable cause) {
        super(cause);
    }

}
