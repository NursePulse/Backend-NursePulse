package com.brainspark.nursepulse.platform.shared.interfaces.rest;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponse handleException(MethodArgumentNotValidException exception, Locale locale) {
        String prefix = messageSource.getMessage("errors.found", null, locale);
        String fields = exception.getFieldErrors().stream()
                .map(fieldError -> messageSource.getMessage(fieldError, locale))
                .collect(Collectors.joining(", "));
        log.warn("Validation failed: {}", fields);
        return ErrorResponse.create(
                exception,
                HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()),
                prefix + " " + fields
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponse handleException(IllegalArgumentException exception, Locale locale) {
        String messageKey = exception.getMessage() != null ? exception.getMessage() : "errors.found";
        String message = Objects.requireNonNullElse(messageSource.getMessage(messageKey, null, messageKey, locale), messageKey);
        log.warn("Illegal argument: {}", message);
        return ErrorResponse.create(exception, HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()), message);
    }

    /**
     * Catch-all for any exception not handled by a more specific handler above.
     * Without this, an uncaught exception falls through to Spring Boot's default
     * error page, which serializes the full stack trace into the JSON response body
     * — leaking internal implementation details and breaking the API's otherwise
     * consistent error contract (see TS-06).
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ErrorResponse handleException(Exception exception, Locale locale) {
        log.error("Unexpected error", exception);
        String message = messageSource.getMessage("error.unexpected.message", null, "Unexpected error", locale);
        return ErrorResponse.create(exception, HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), message);
    }

}