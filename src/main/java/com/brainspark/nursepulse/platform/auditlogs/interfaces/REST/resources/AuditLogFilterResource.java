package com.brainspark.nursepulse.platform.auditlogs.interfaces.REST.resources;

import com.brainspark.nursepulse.platform.auditlogs.domain.model.valueobjects.AuditedEntityType;
import com.brainspark.nursepulse.platform.auditlogs.domain.model.valueobjects.AuditActionType;

import io.swagger.v3.oas.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

/**
 * Holds the optional query parameters accepted by {@code GET /api/v1/audit-logs}.
 * Every field is optional so any combination of filters (including none) is valid
 * @param patientId filter by patient identifier
 * @param entityType filter by audited entity type
 * @param entityId filter by audited entity identifier
 * @param actionType filter by action type
 * @param performedBy filter by the staff member who performed the action
 * @param from inclusive lower bound for {@code performedAt}, ISO-8601
 * @param to inclusive upper bound for {code performedAt}, ISO-8601
 * @param page zero based page index, defaults to 0
 * @param size page size, defaults to 20, capped at 200 max
 */
@Schema(description= "Optional filters and pagination parameters for listing audit log entries")
public record AuditLogFilterResource(
        @Nullable
        @Schema(description = "Filter by patient identifier", example = "11")
        Long patientId,

        @Nullable
        @Schema(description = "Filter by audited entity type", example = "VITAL_SIGNS")
        AuditedEntityType entityType,

        @Nullable
        @Schema(description = "Filer by audited entity identifier", example = "8e4r0k")
        String entityId,

        @Nullable
        @Schema(description = "Filter by action type", example = "CREATE")
        AuditActionType actionType,

        @Nullable
        @Schema(description = "Filter by the staff member that performed the action", example = "nurse-user-123")
        String performedBy,

        @Nullable
        @Schema(description = "Inclusive lower bound for performedAt", example = "2026-01-15T00:00:02Z")
        Instant from,

        @Nullable
        @Schema(description = "Inclusive upper bound for performedAt", example = "2026-01-20T23:59:59Z")
        Instant to,

        @Nullable
        @Schema(description = "Zero based page index", example= "0", defaultValue = "0")
        Integer page,

        @Nullable
        @Schema(description = "Page size, capped at 200 to keep queries efficient and clean", example= "20", defaultValue = "20")
        Integer size
) {
}
