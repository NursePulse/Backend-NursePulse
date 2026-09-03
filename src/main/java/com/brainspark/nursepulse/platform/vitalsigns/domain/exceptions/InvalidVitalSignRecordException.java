package com.brainspark.nursepulse.platform.vitalsigns.domain.exceptions;

/**
 * Exception thrown when a vital sign record contains invalid data.
 *
 * @summary
 * This exception is thrown when a vital sign record violates a domain rule.
 *
 * @see RuntimeException
 */
public class InvalidVitalSignRecordException extends RuntimeException {
    /**
     * Constructor for the exception.
     *
     * @param message the reason why the vital sign record is invalid
     */
    public InvalidVitalSignRecordException(String message) {
        super("Invalid vital sign record: %s".formatted(message));
    }
}
