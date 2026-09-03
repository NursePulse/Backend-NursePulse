package com.brainspark.nursepulse.platform.vitalsigns.domain.exceptions;

/**
 * Exception thrown when a vital sign record is not found.
 *
 * @summary
 * This exception is thrown when a vital sign record cannot be found by its ID.
 *
 * @see RuntimeException
 */
public class VitalSignRecordNotFoundException extends RuntimeException {
    /**
     * Constructor for the exception.
     *
     * @param vitalSignRecordId the ID of the vital sign record that was not found
     */
    public VitalSignRecordNotFoundException(Long vitalSignRecordId) {
        super("Vital sign record with ID %s not found.".formatted(vitalSignRecordId));
    }
}
