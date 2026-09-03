package com.brainspark.nursepulse.platform.auditlogs.interfaces.REST.resources;

import com.brainspark.nursepulse.platform.auditlogs.domain.model.valueobjects.AuditedEntityType;
import com.brainspark.nursepulse.platform.auditlogs.domain.model.valueobjects.AuditActionType;

import io.swagger.v3.oas.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.Map;

/**
 * Outbound resource representing a single event in the audit history of a clinical entity.
 * Unlike {@link AuditLogTimelineItemResource} this resource includes {@code patientId} because entity history is not inherently patient scoped.
 * The same entity type/id combination can involve multiple patients or none at all.
 * Metadata is already parsed as a {@code Map<String, Object>} so our frontend detail screen (vital sign detail, SBAR detail, clinical event detail, etc)
 * can render contextual information without and extra parsing step.
 * @param id unique identifier of the audit log entry
 * @param patientId patient context of this event
 * @param entityType the category of the audited clinical entity
 * @param entityId the opaque identifier of the audited entity instance
 * @param actionType the nature of the clinical action
 * @param performedBy the staff member who performed the action
 * @param performedAt the exact instant the action occurred (with ISO-8601)
 * @param metadata optional contextual details parsed from the stores JSON string
 */
@Schema(description="A single event in the chronological audit history of a clinical entity")
public record AuditLogHistoryItemResource(
        @Schema(description = "Unique identifier of the audit log entry", example= "19")
        Long id,

        @Nullable
        @Schema(description = "Patient context of this event", example="15")
        Long patientId,

        @Schema(description = "Type of the audited clinical entity", example= "VITAL_SIGNS")
        AuditedEntityType entityType,

        @Schema(description = "Identifier of the audited entity instance", example="8fr94t")
        String entityId,

        @Schema(description = "Type of the clinical action", example = "CREATE")
        AuditActionType actionType,

        @Schema(description= "Staff member who performed the action", example="nurse-user-4")
        String performedBy,

        @Schema(description = "Instant the action occurred (using ISO-8601)", example="2026-04-01T14:30:00Z")
        Instant performedAt,

        @Nullable
        @Schema(description = "Contextual details parsed from the stored metadata JSON string",
        example = "{\"systolic\":120,\"diastolic\":80}")
        Map<String, Object> metadata
) {
}
