package com.brainspark.nursepulse.platform.auditlogs.domain.model.commands;

import com.brainspark.nursepulse.platform.auditlogs.domain.model.valueobjects.AuditActionType;
import com.brainspark.nursepulse.platform.auditlogs.domain.model.valueobjects.AuditedEntityType;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

/**
 * Command that carries the intent to persist a new, immutable audit log entry.
 *
 * @param patientId    nullable — absent when the action is global (not patient-scoped)
 * @param entityType   the type of domain entity that was acted upon
 * @param entityId     opaque identifier of that entity (String to stay bounded-context-agnostic)
 * @param actionType   the nature of the clinical action
 * @param performedBy  identifier of the actor (user id, system service name, etc.)
 * @param performedAt  exact instant the action occurred; defaults to now when null at the service layer
 * @param metadata     optional free-form JSON detail string
 */
public record CreateAuditLogCommand(
        @Nullable Long patientId,
        AuditedEntityType entityType,
        String entityId,
        AuditActionType actionType,
        String performedBy,
        @Nullable Instant performedAt,
        @Nullable String metadata
) {}