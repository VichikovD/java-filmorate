package ru.yandex.practicum.filmorate.exception;

public class ValidateFilmException extends Throwable {
    public ValidateFilmException(String message) {
        super(message);
    }

    public ValidateFilmException(String message, Throwable cause) {
        super(message, cause);
    }
}
