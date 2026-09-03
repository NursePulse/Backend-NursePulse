package com.brainspark.nursepulse.platform.auditlogs.interfaces.REST.resources;

import com.brainspark.nursepulse.platform.auditlogs.domain.model.valueobjects.AuditedEntityType;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Outbound resource that wraps the chronological audit history of a specific clinical entity
 * The envelope includes entity level metadata ({@code entityType}, {@code entityId}, {@code eventCount}) so our frontend can render
 * the history header without reading through the event list. Events are always ordered oldest to newest (ascending {@code performedAt}), making
 * the evolution of the entity readable from top to bottom.
 * @param entityType the category of the audited clinical entity
 * @param entityId the opaque identifier of the audited entity instance
 * @param eventCount total number of events in this response
 * @param events the chronologically ordered list of audit events (oldest to newest)
 */
@Schema(description = "Chronological audit history for a specific clinical entity")
public record AuditLogHistoryResource(
        @Schema(description="Type of the audited clinical entity", example="VITAL_SIGNS")
        AuditedEntityType entityType,

        @Schema(description = "Identifier of the audited entity instance", example="8eg4ht")
        String entityId,

        @Schema(description = "Total number of events in this response", example= "5")
        int eventCount,

        @Schema(description = "Chronologically ordered audit events for this entity (oldest to newest)")
        List<AuditLogHistoryItemResource> events
) {
}
