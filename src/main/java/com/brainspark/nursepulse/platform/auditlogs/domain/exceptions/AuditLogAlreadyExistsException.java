package com.brainspark.nursepulse.platform.auditlogs.domain.exceptions;

/**
 * Thrown when an attempt is made to persist an audit log entry that would violate
 * an idempotency constraint (example: duplicate external correlation id).
 *
 */
public class AuditLogAlreadyExistsException extends RuntimeException {

    public AuditLogAlreadyExistsException(String correlationId) {
        super("An audit log entry with correlation id '%s' already exists.".formatted(correlationId));
    }
}