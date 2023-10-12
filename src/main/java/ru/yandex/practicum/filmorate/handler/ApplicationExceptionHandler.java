package ru.yandex.practicum.filmorate.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.yandex.practicum.filmorate.exception.InvalidIdException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

import javax.validation.ConstraintViolationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ApplicationExceptionHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleInvalidIdException(EmptyResultDataAccessException e) {
        String errorMessage = e.getMessage();
        log.error("EmptyResultDataAccessException = " + errorMessage, e);
        return new ErrorResponse("Data Base doesn't contain requested data", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        HashMap<String, String> errorMap = new HashMap<>();
        for (FieldError error : e.getFieldErrors()) {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errorMap.put(fieldName, errorMessage);
            log.debug("{} = {}", fieldName, errorMessage);
        }
        return errorMap;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidIdException(InvalidIdException e) {
        String errorMessage = e.getMessage();
        log.debug("ID Exception = {}", errorMessage);
        return new ErrorResponse("ID Exception", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        String errorMessage = e.getMessage();
        log.debug("Not Found Exception = {}", errorMessage);
        return new ErrorResponse("Not Found Exception", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(ConstraintViolationException e) {
        String errorMessage = e.getMessage();
        log.debug("Not valid request = {}", errorMessage);
        return new ErrorResponse("Not valid request", getFieldName(errorMessage));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String errorMessage = e.getMessage();
        log.debug("{} = {}", "Invalid parameter: " + e.getName(), errorMessage);
        return new ErrorResponse("Not valid request",
                String.format("Please check if parameter - %s is correct", e.getName().toUpperCase()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUndefinedException(Exception e) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        String errorMessage = e.getMessage();
        String stackTrace = stringWriter.toString();
        log.error("Exception = {}", errorMessage, e);
        return new ErrorResponse(e.toString(), errorMessage, stackTrace);
    }

    private String getFieldName(String string) {
        return string.split("\\.")[1];
    }
}
