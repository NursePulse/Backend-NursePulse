package com.brainspark.nursepulse.platform.auditlogs.domain.services;

import com.brainspark.nursepulse.platform.shared.application.result.ApplicationError;
import com.brainspark.nursepulse.platform.shared.application.result.Result;
import com.brainspark.nursepulse.platform.auditlogs.domain.model.aggregates.AuditLog;
import com.brainspark.nursepulse.platform.auditlogs.domain.model.commands.CreateAuditLogCommand;

/**
 * Domain service interface for write operations on the {@link AuditLog} aggregate.
 */
public interface AuditLogCommandService {

    /**
     * Handles the creation of a new, immutable audit log entry.
     *
     * @param command the creation command
     * @return {@link Result.Success} containing the persisted {@link AuditLog},
     *         or {@link Result.Failure} with an {@link ApplicationError} on any error
     */
    Result<AuditLog, ApplicationError> handle(CreateAuditLogCommand command);
}