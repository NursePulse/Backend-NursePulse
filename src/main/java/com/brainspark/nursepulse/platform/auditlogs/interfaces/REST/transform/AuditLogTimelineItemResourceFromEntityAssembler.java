package com.brainspark.nursepulse.platform.auditlogs.interfaces.REST.transform;

import com.brainspark.nursepulse.platform.auditlogs.domain.model.aggregates.AuditLog;
import com.brainspark.nursepulse.platform.auditlogs.interfaces.REST.resources.AuditLogTimelineItemResource;

/**
 * This represents a stateless assembler that maps a persisted {@link AuditLog} entity to an {@link AuditLogTimelineItemResource} for use
 * in the patient timeline endpoint.
 * {@code patientId} is intentionally omitted from the resource because it is redundant on a patient scoped response.
 */
public final class AuditLogTimelineItemResourceFromEntityAssembler {
    private AuditLogTimelineItemResourceFromEntityAssembler(){}

    public static AuditLogTimelineItemResource toResourceFromEntity(AuditLog entity){
        return new AuditLogTimelineItemResource(
                entity.getId(),
                entity.getEntityType(),
                entity.getEntityId(),
                entity.getActionType(),
                entity.getPerformedBy(),
                entity.getPerformedAt(),
                AuditLogMetadataDeserializer.deserialize(entity.getMetadata().getValue())
        );
    }
}

