package com.brainspark.nursepulse.platform.auditlogs.domain.exceptions;

import com.brainspark.nursepulse.platform.auditlogs.domain.model.aggregates.AuditLog;

/**
 * Exception thrown when an audit log entry cannot be found by its attached identifier.
 * The {@code GET /api/v1/audit-logs/{auditLogId}} endpoint does not throw this exception directly.
 * Instead, the query service returns a {@code Result.Failure} carrying an {@code ApplicationError.notFound(...)},
 * which {@code ErrorResponseAssembler} maps as a 404 http error.
 * @see RuntimeException
 */
public class AuditLogNotFoundException extends RuntimeException{

    /**
     * @param auditLogId the identifier of the audit log entry that was not found
     */
    public AuditLogNotFoundException(Long auditLogId) {
        super("Audit log entry with identifier %s not found".formatted(auditLogId));
    }
}
