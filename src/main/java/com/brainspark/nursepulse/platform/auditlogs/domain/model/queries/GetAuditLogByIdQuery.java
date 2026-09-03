package com.brainspark.nursepulse.platform.auditlogs.domain.model.queries;

import com.brainspark.nursepulse.platform.auditlogs.domain.exceptions.InvalidAuditLogException;

/**
 * This record represents a query that carries the intent retrieve a single audit log entry by its identifier.
 * @param auditLogId identifier of the audit log entry to retrieve. It must be a positive number (greater than zero).
 */
public record GetAuditLogByIdQuery(Long auditLogId) {
    public GetAuditLogByIdQuery{
        if (auditLogId == null || auditLogId <= 0)
            throw new InvalidAuditLogException("Audit log identifier must be greater than zero");
    }
}
