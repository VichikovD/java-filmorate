package ru.yandex.practicum.filmorate.exception;

public class ValidateUserException extends Throwable {
    public ValidateUserException(String message) {
        super(message);
    }

    public ValidateUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
