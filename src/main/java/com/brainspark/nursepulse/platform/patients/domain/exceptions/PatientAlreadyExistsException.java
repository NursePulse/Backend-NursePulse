package com.brainspark.nursepulse.platform.patients.domain.exceptions;

public class PatientAlreadyExistsException extends RuntimeException {

    public PatientAlreadyExistsException(String documentNumber) {
        super("Patient with document number %s already exists.".formatted(documentNumber));
    }
}
