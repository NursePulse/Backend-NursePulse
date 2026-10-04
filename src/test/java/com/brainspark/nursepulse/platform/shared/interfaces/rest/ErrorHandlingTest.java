package com.brainspark.nursepulse.platform.shared.interfaces.rest;

import com.brainspark.nursepulse.platform.shared.application.result.ApplicationError;
import com.brainspark.nursepulse.platform.shared.interfaces.rest.transform.ErrorResponseAssembler;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.http.HttpStatus;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/** TS-06: consistent error responses (status code + code/message/details body). */
class ErrorHandlingTest {

    @Test
    void mapsEachApplicationErrorToItsHttpStatus() {
        assertEquals(400, ErrorResponseAssembler.toStatusFromErrorCode("VALIDATION_ERROR").value());
        assertEquals(404, ErrorResponseAssembler.toStatusFromErrorCode("PATIENT_NOT_FOUND").value());
        assertEquals(409, ErrorResponseAssembler.toStatusFromErrorCode("PATIENT_CONFLICT").value());
        assertEquals(422, ErrorResponseAssembler.toStatusFromErrorCode("BUSINESS_RULE_VIOLATION").value());
        assertEquals(500, ErrorResponseAssembler.toStatusFromErrorCode("UNEXPECTED_ERROR").value());
        assertEquals(500, ErrorResponseAssembler.toStatusFromErrorCode("SOMETHING_ELSE").value());
    }

    @Test
    void bodyKeepsCodeAndDetailsAndAlwaysHasAMessage() {
        var response = ErrorResponseAssembler.toErrorResponseFromApplicationError(
                ApplicationError.validationError("username", "Invalid username or password"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        var body = response.getBody();
        assertNotNull(body);
        assertEquals("VALIDATION_ERROR", body.code());
        assertEquals("Invalid username or password", body.details());
        assertFalse(body.message().isBlank());
    }

    @Test
    void notFoundErrorsAnswer404WithTheResourceCode() {
        var response = ErrorResponseAssembler.toErrorResponseFromApplicationError(
                ApplicationError.notFound("Patient", "7"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("PATIENT_NOT_FOUND", response.getBody().code());
    }

    private GlobalExceptionHandler handler() {
        var messages = new StaticMessageSource();
        messages.addMessage("errors.found", Locale.ROOT, "Errors found:");
        messages.addMessage("error.unexpected.message", Locale.ROOT, "Unexpected error");
        return new GlobalExceptionHandler(messages);
    }

    @Test
    void illegalArgumentBecomesBadRequestWithItsMessage() {
        var response = handler().handleException(new IllegalArgumentException("situation cannot be null or blank"), Locale.ROOT);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("situation cannot be null or blank", response.getBody().getDetail());
    }

    @Test
    void unexpectedExceptionBecomesInternalServerErrorWithoutLeakingDetails() {
        var response = handler().handleException(new Exception("db password leaked"), Locale.ROOT);

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Unexpected error", response.getBody().getDetail());
    }
}
