package com.brainspark.nursepulse.platform.patients.domain.exceptions;

public class InvalidPatientException extends RuntimeException {

    public InvalidPatientException(String message) {
        super("Invalid patient: %s".formatted(message));
    }
}
