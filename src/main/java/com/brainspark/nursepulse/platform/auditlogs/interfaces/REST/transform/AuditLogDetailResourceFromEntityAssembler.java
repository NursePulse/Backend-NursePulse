package com.brainspark.nursepulse.platform.auditlogs.interfaces.REST.transform;

import com.brainspark.nursepulse.platform.auditlogs.domain.model.aggregates.AuditLog;
import com.brainspark.nursepulse.platform.auditlogs.interfaces.REST.resources.AuditLogDetailResource;

import java.util.HashMap;

/**
 * This represents a stateless assembler that maps a persisted {@link AuditLog} entity to the richer {@link AuditLogDetailResource} used by
 * the single entry detail endpoint.
 * Unlike {@link AuditLogResourceFromEntityAssembler}, this assembler parses the stored metadata JSON string back into a {@code Map<String, Object>} so
 * our frontend can reder contextual details directly without an extra parsing step.
 */
public final class AuditLogDetailResourceFromEntityAssembler{
    private AuditLogDetailResourceFromEntityAssembler(){}

    public static AuditLogDetailResource toResourceFromEntity(AuditLog entity){
        return new AuditLogDetailResource(
                entity.getId(),
                entity.getPatientId(),
                entity.getPatientId() != null,
                entity.getEntityType(),
                entity.getEntityId(),
                entity.getActionType(),
                entity.getPerformedBy(),
                entity.getPerformedAt(),
                AuditLogMetadataDeserializer.deserialize(entity.getMetadata().getValue()),
                entity.getCreatedAt(),
                entity.getUpdateAt()
        );
    }
}