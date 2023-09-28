package ru.yandex.practicum.filmorate.model;

public class ErrorResponse {
    private final String error;
    private final String message;
    private String stackTraceElement;

    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
    }

    public ErrorResponse(String error, String message, String stackTraceElement) {
        this.error = error;
        this.message = message;
        this.stackTraceElement = stackTraceElement;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getStackTraceElement() {
        return stackTraceElement;
    }

    public void setStackTraceElement(String stackTraceElement) {
        this.stackTraceElement = stackTraceElement;
    }
}