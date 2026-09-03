package com.brainspark.nursepulse.platform.auditlogs.domain.model.queries;

import com.brainspark.nursepulse.platform.auditlogs.domain.exceptions.InvalidAuditLogException;

import org.jspecify.annotations.Nullable;
import java.time.Instant;

/**
 * This record represents a query that carries the intent to retrieve the full chronological audit timeline of a given patient.
 * All filter fields beyond {@code patientId} are optional. {@code from} and {@code to} allow caller to window the timeline to a
 * clinical period of time without requiring a separate paginated request.
 * @param patientId the identifier of the patient whose audit timeline is being requested. It must be a positive number.
 * @param from optional inclusive lower bound on {@code performedAt}. Omit for an open-start timeline
 * @param to optional inclusive upper bound to {@code performedAt}. Omit for an open-end timeline
 */
public record GetPatientAuditTimelineQuery(
        Long patientId,
        @Nullable Instant from,
        @Nullable Instant to
) {
    public GetPatientAuditTimelineQuery{
        if(patientId == null || patientId <= 0)
            throw new InvalidAuditLogException("Patient identifier must be a positive number (greater than zero)");
    }
}
