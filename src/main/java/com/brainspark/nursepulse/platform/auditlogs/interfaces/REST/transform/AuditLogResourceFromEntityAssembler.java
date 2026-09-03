package com.brainspark.nursepulse.platform.auditlogs.interfaces.REST.transform;

import com.brainspark.nursepulse.platform.auditlogs.domain.model.aggregates.AuditLog;
import com.brainspark.nursepulse.platform.auditlogs.interfaces.REST.resources.AuditLogResource;

/**
 * Stateless assembler that maps a persisted {@link AuditLog} entity to the outbound REST resource.
 */
public final class AuditLogResourceFromEntityAssembler {

    private AuditLogResourceFromEntityAssembler() {}

    public static AuditLogResource toResourceFromEntity(AuditLog entity) {
        return new AuditLogResource(
                entity.getId(),
                entity.getPatientId(),
                entity.getEntityType(),
                entity.getEntityId(),
                entity.getActionType(),
                entity.getPerformedBy(),
                entity.getPerformedAt(),
                entity.getMetadata().getValue(),
                entity.getCreatedAt()
        );
    }
}