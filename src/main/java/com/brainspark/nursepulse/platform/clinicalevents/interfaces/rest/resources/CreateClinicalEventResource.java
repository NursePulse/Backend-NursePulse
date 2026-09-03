package com.brainspark.nursepulse.platform.clinicalevents.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Resource received to register a clinical event.
 */
@Schema(
    name = "CreateClinicalEventRequest",
    description = "Clinical event registration request",
    example = "{\"patientId\": 1, \"eventType\": \"OBSERVATION\", \"severity\": \"LOW\", " +
            "\"title\": \"Saturación baja durante monitoreo\", \"description\": \"SpO2 en 90% durante la ronda nocturna.\"}"
)
public record CreateClinicalEventResource(
    @NotNull(message = "{validation.not-blank}")
    @Schema(description = "Patient identifier", example = "1")
    Long patientId,

    @NotBlank(message = "{validation.not-blank}")
    @Schema(
        description = "Clinical event type",
        example = "OBSERVATION",
        allowableValues = {"MEDICATION", "PROCEDURE", "CONDITION_CHANGE", "COMPLICATION", "EMERGENCY", "OBSERVATION"}
    )
    String eventType,

    @NotBlank(message = "{validation.not-blank}")
    @Schema(
        description = "Clinical event severity",
        example = "LOW",
        allowableValues = {"LOW", "MODERATE", "HIGH", "CRITICAL"}
    )
    String severity,

    @NotBlank(message = "{validation.not-blank}")
    @Size(min = 3, max = 120, message = "{validation.size}")
    @Schema(description = "Short event title", example = "Saturación baja durante monitoreo")
    String title,

    @NotBlank(message = "{validation.not-blank}")
    @Size(min = 3, max = 1000, message = "{validation.size}")
    @Schema(description = "Clinical description of the event", example = "SpO2 en 90% durante la ronda nocturna.")
    String description
) {
}
