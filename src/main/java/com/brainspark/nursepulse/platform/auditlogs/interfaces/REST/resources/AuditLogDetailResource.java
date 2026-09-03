package com.brainspark.nursepulse.platform.auditlogs.interfaces.REST.resources;

import com.brainspark.nursepulse.platform.auditlogs.domain.model.valueobjects.AuditActionType;
import com.brainspark.nursepulse.platform.auditlogs.domain.model.valueobjects.AuditedEntityType;

import io.swagger.v3.oas.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.Map;
import java.util.Date;

@Schema(description= "Full detail of a single, persisted audit log entry")
public record AuditLogDetailResource(
        @Schema(description = "Unique identifier of the audit log entry", example = "2")
        Long id,

        @Nullable
        @Schema(description = "Patient identifier", example = "24")
        Long patientId,

        @Schema(description = "Whether this entry is scoped to a specific patient", example= "true")
        boolean patientScoped,

        @Schema(description = "Type of the audited entity", example = "VITAL_SIGNS")
        AuditedEntityType entityType,

        @Schema(description = "Identifier of the audited entity", example = "1e4y0v")
        String entityId,

        @Schema(description = "Type of the clinical action", example = "CREATE")
        AuditActionType actionType,

        @Schema(description = "Staff member who performed the action", example = "nurse-user-3")
        String performedBy,

        @Schema(description = "Instant the action occurred", example="2026-06-01T14:30:15Z")
        Instant performedAt,

        @Schema(description = "Optional metadata which is parsed as a JSON object", example= """
                {
                    "systolic": 120,
                    "diastolic": 80,
                    "notes": "Patient collaborate through the procedure and tolerated it well."
                }
                """)
        Map<String, Object> metadata,

        @Schema(description = "Server timestamp of when this record was created")
        Date createdAt,

        @Schema(description = "Server timestamp of the last modification or update")
        Date updatedAt
) {
}
