package ru.yandex.practicum.filmorate.handler;

import lombok.extern.slf4j.Slf4j;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ApplicationExceptionHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        HashMap<String, String> errorMap = new HashMap<>();
        for (FieldError error : e.getFieldErrors()) {
            String fieledName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errorMap.put(fieledName, errorMessage);
            getLogged(fieledName, errorMessage);
        }
        return errorMap;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidIdException(InvalidIdException e) {
        String errorMessage = e.getMessage();
        getLogged("ID Exception", errorMessage);
        return new ErrorResponse("ID Exception", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        String errorMessage = e.getMessage();
        getLogged("Not Found Exception", errorMessage);
        return new ErrorResponse("Not Found Exception", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(ConstraintViolationException e) {
        String errorMessage = e.getMessage();
        getLogged("Not valid request", errorMessage);
        return new ErrorResponse("Not valid request", getFieldName(errorMessage));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String errorMessage = e.getMessage();
        getLogged("Invalid parameter: " + e.getName(), errorMessage);
        return new ErrorResponse("Not valid request",
                String.format("Please check if parameter - %s is correct", e.getName().toUpperCase()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUndefinedException(Exception e) {
        String errorMessage = e.getMessage();
        String stackTrace = Arrays.toString(e.getStackTrace());
        getLogged("Exception", errorMessage, stackTrace);
        return new ErrorResponse(e.toString(), errorMessage, stackTrace);
    }

    private String getFieldName(String string) {
        return string.split("\\.")[1];
    }

    private void getLogged(String name, String message) {
        log.debug("{} = {}", name, message);
    }

    private void getLogged(String name, String message, String stackTrace) {
        log.debug("{} = {} \nStackTrace: {}", name, message, stackTrace);
    }
}
