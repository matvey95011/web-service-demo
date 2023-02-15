package ru.example.java.spring.demo.app.controller;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.example.java.spring.demo.app.exception.EntityExistsApplicationException;
import ru.example.java.spring.demo.app.exception.EntityNotFoundApplicationException;
import ru.example.java.spring.demo.app.exception.IllegalArgumentApplicationException;
import ru.example.java.spring.demo.app.exception.model.ErrorDescription;
import ru.example.java.spring.demo.app.exception.model.ErrorResponse;
import ru.example.java.spring.demo.app.exception.model.ValidationErrorResponse;

import java.util.List;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@Slf4j
@Hidden
@RestControllerAdvice(annotations = RestController.class)
public class ExceptionController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ValidationErrorResponse badRequest(MethodArgumentNotValidException exception) {
        final BindingResult bindingResult = exception.getBindingResult();
        return new ValidationErrorResponse(buildMessage(bindingResult), buildErrors(bindingResult));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentApplicationException.class)
    public ErrorResponse badRequest(IllegalArgumentApplicationException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UsernameNotFoundException.class)
    public ErrorResponse unauthorized(UsernameNotFoundException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundApplicationException.class)
    public ErrorResponse notFound(EntityNotFoundApplicationException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EntityExistsApplicationException.class)
    public ErrorResponse conflict(EntityExistsApplicationException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(IllegalStateException.class)
    public ErrorResponse conflict(IllegalStateException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RuntimeException.class)
    public ErrorResponse error(RuntimeException exception) {
        log.error("", exception);
        return new ErrorResponse(exception.getMessage());
    }

    private String buildMessage(BindingResult bindingResult) {
        return String.format("Error on %s, rejected errors [%s]",
                bindingResult.getTarget(),
                bindingResult.getAllErrors()
                        .stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .collect(joining(";")));
    }

    private List<ErrorDescription> buildErrors(BindingResult bindingResult) {
        return bindingResult.getFieldErrors()
                .stream()
                .map(e -> new ErrorDescription(e.getField(), e.getDefaultMessage()))
                .collect(toList());
    }


}
