package com.brainspark.nursepulse.platform.auditlogs.domain.exceptions;

/**
 * Exception thrown when an audit log query is constructed with invalid parameters, for example, a non-positive identifier such as -5.
 * @see RuntimeException
 */
public class InvalidAuditLogException extends RuntimeException{
    /**
     * @param message the reason why the audit log query is invalid
     */
    public InvalidAuditLogException(String message){
        super("Invalid audit log query: %s".formatted(message));
    }
}
