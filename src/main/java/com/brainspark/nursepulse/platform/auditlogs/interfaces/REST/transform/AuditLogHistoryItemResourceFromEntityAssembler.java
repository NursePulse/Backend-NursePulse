package com.brainspark.nursepulse.platform.auditlogs.interfaces.REST.transform;

import com.brainspark.nursepulse.platform.auditlogs.domain.model.aggregates.AuditLog;
import com.brainspark.nursepulse.platform.auditlogs.interfaces.REST.resources.AuditLogHistoryItemResource;

/**
 * Stateless assembler that maps a persisted {@link AuditLog} entity to an {@link AuditLogHistoryItemResource} for use in the entity audit history endpoint.
 * Unlike {@link AuditLogTimelineItemResourceFromEntityAssembler}, this assembler includes {@code patientId} because entity history
 * is not inherently patient scoped
 */
public final class AuditLogHistoryItemResourceFromEntityAssembler {
    private AuditLogHistoryItemResourceFromEntityAssembler(){}

    public static AuditLogHistoryItemResource toResourceFromEntity(AuditLog entity){
        return new AuditLogHistoryItemResource(
                entity.getId(),
                entity.getPatientId(),
                entity.getEntityType(),
                entity.getEntityId(),
                entity.getActionType(),
                entity.getPerformedBy(),
                entity.getPerformedAt(),
                AuditLogMetadataDeserializer.deserialize(entity.getMetadata().getValue())
        );
    }
}
