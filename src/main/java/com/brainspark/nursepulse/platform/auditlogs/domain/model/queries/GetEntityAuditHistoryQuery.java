package com.brainspark.nursepulse.platform.auditlogs.domain.model.queries;

import com.brainspark.nursepulse.platform.auditlogs.domain.exceptions.InvalidAuditLogException;
import com.brainspark.nursepulse.platform.auditlogs.domain.model.valueobjects.AuditedEntityType;

/**
 * Record that represents a query that carries the intent to retrieve the chronological audit history of a specific clinical entity.
 * It is designed to be consumed from any entity detail screen (patient, vital signs, SBAR handover, clinical events, alert) that needs to show
 * the evolution of that entity over time.
 * @param entityType the category of the clinical entity whose history is requested
 * @param entityId the opaque identifier of that specific entity instance. It must not be blank
 */
public record GetEntityAuditHistoryQuery(AuditedEntityType entityType, String entityId) {
    public GetEntityAuditHistoryQuery{
        if(entityType == null)
            throw new InvalidAuditLogException("Entity type must not be null");
        if (entityId == null || entityId.isBlank())
            throw new InvalidAuditLogException("Entity identifier must not be blank");
    }
}
