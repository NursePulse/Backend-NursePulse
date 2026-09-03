package com.brainspark.nursepulse.platform.clinicalevents.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Resource that represents a clinical event.
 */
@Schema(
    name = "ClinicalEventResource",
    description = "Registered clinical event",
    example = "{\"id\": 1, \"patientId\": 1, \"eventType\": \"OBSERVATION\", \"severity\": \"LOW\", " +
            "\"title\": \"Saturación baja durante monitoreo\", \"description\": \"SpO2 en 90%.\", " +
            "\"registeredBy\": \"nurse.maria\", \"occurredAt\": \"2026-07-05T10:42:00\"}"
)
public record ClinicalEventResource(
    Long id,
    Long patientId,
    String eventType,
    String severity,
    String title,
    String description,
    String registeredBy,
    String occurredAt
) {
}
