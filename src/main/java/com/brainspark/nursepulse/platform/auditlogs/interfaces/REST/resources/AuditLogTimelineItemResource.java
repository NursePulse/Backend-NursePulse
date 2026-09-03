package com.brainspark.nursepulse.platform.auditlogs.interfaces.REST.resources;

import com.brainspark.nursepulse.platform.auditlogs.domain.model.valueobjects.AuditActionType;
import com.brainspark.nursepulse.platform.auditlogs.domain.model.valueobjects.AuditedEntityType;

import io.swagger.v3.oas.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.Map;

/**
 * Outbound resource representing a single event in a patient's audit timeline.
 * Shaped for direct consumption by a patient detail screen or timeline component:
 * {@code metadata} is already parsed as a {@code Map<String, Object>} so our frontend can render contextual details without
 * a parsing step. {@code patientId} is omitted because it is redundant on a patient-scoped response.
 *
 * @param id          unique identifier of the audit log entry
 * @param entityType  the category of the clinical entity that was acted upon
 * @param entityId    the opaque identifier of that entity
 * @param actionType  the nature of the clinical action
 * @param performedBy the staff member who performed the action
 * @param performedAt the exact instant the action occurred (using ISO-8601)
 * @param metadata    optional contextual details parsed from the stored JSON string
 */
@Schema(description = "A single clinical event in a patient's audit timeline")
public record AuditLogTimelineItemResource(

        @Schema(description = "Unique identifier of the audit log entry", example = "7")
        Long id,

        @Schema(description = "Type of the audited clinical entity", example = "VITAL_SIGNS")
        AuditedEntityType entityType,

        @Schema(description = "Identifier of the audited entity", example = "8fe45t")
        String entityId,

        @Schema(description = "Type of the clinical action", example = "CREATE")
        AuditActionType actionType,

        @Schema(description = "Staff member who performed the action", example = "nurse-user-99")
        String performedBy,

        @Schema(description = "Instant the action occurred", example = "2026-04-29T14:30:00Z")
        Instant performedAt,

        @Nullable
        @Schema(description = "Contextual details, parsed from the stored metadata JSON string",
                example = "{\"systolic\":120,\"diastolic\":80}")
        Map<String, Object> metadata
) {

}