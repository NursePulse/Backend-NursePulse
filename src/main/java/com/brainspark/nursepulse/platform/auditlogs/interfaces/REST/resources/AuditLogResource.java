package com.brainspark.nursepulse.platform.auditlogs.interfaces.REST.resources;

import com.brainspark.nursepulse.platform.auditlogs.domain.model.valueobjects.AuditActionType;
import com.brainspark.nursepulse.platform.auditlogs.domain.model.valueobjects.AuditedEntityType;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.Date;

/**
 * Outbound REST resource representing a persisted audit log entry.
 * Note: the performedAt field is an Instant (from the command) while createdAt is a Date (from the database), to illustrate handling of different temporal types.
 *
 * @Schema and its parameters are used for swagger documentation, providing descriptions and examples for each field in the API docs.
 */
@Schema(description = "Representation of a persisted audit log entry")
public record AuditLogResource(

        @Schema(description = "Unique identifier of the audit log entry", example = "1")
        Long id,

        @Nullable
        @Schema(description = "Patient identifier, null for global actions", example = "15")
        Long patientId,

        @Schema(description = "Type of the audited entity", example = "VITAL_SIGNS")
        AuditedEntityType entityType,

        @Schema(description = "Identifier of the audited entity", example = "8fe45t")
        String entityId,

        @Schema(description = "Type of the clinical action", example = "CREATE")
        AuditActionType actionType,

        @Schema(description = "Staff member who performed the action", example = "nurse-user-1")
        String performedBy,

        @Schema(description = "Instant the action occurred", example = "2026-06-01T14:30:00Z")
        Instant performedAt,

        @Nullable
        @Schema(description = "Optional metadata JSON string")
        String metadata,

        @Schema(description = "Server timestamp of when this record was created")
        Date createdAt
) {}