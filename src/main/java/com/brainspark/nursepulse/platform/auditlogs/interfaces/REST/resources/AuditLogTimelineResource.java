package com.brainspark.nursepulse.platform.auditlogs.interfaces.REST.resources;

import org.jspecify.annotations.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

/**
 * Outbound resource that wraps the ordered list of clinical events for a patient's audit timeline.
 * The envelope includes patient level metadata ({@code patientId} ), the actual time window the data convers, and an {@code eventCount} so our frontend
 * can reder the timeline header without reading through the event list.
 * Events are always ordered oldest to newest (using {@code performedAt}) to match the natural left to right reading direction of a timeline component.
 * @param patientId the patient whose events are listed
 * @param from the effective lower bound of the time window (it's null when no lower bound was requester)
 * @param to the effective upper bound of the time windows (it's null when no upper bound was requested)
 * @param eventCount total number of events in this response
 * @param events the ordered list of timeline events (oldest to newest).
 */
@Schema(description = "Chronological audit timeline for a patient")
public record AuditLogTimelineResource(
        @Schema(description = "Patient identifier", example = "123")
        Long patientId,

        @Nullable
        @Schema(description = "Inclusive lower bound of the time window applied to this timeline", example= "2026-01-01T06:00:00Z")
        Instant from,

        @Nullable
        @Schema(description = "Inclusive upper bound of the time window applied to this timeline", example="2026-12-31T23:59:59Z")
        Instant to,

        @Schema(description = "Total number of events in this response", example="15")
        int eventCount,

        @Schema(description = "Chronologically ordered list of clinical events (from oldest to newest)")
        List<AuditLogTimelineItemResource> events
) {
}
