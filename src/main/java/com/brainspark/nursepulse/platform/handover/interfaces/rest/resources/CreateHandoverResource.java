package com.brainspark.nursepulse.platform.handover.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;


@Schema(
        name = "CreateHandoverRequest",
        description = "Request payload for creating a new handover",
        example = "{\"patientId\": 1, \"title\": \"Night Shift Handover\", \"description\": \"Pending tasks and key updates for the next shift\"}"
)
public record CreateHandoverResource(
        @Schema(
                description = "Patient ID associated with the handover",
                example = "1"
        )
        Long patientId,

        @Schema(
                description = "Handover title",
                example = "Night Shift Handover",
                minLength = 1,
                maxLength = 255
        )
        String title,

        @Schema(
                description = "Handover description",
                example = "Pending tasks and key updates for the next shift",
                minLength = 1,
                maxLength = 2000
        )
        String description
) {

    public CreateHandoverResource {
        if (patientId == null) {
            throw new IllegalArgumentException("Patient ID is required");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description is required");
        }
    }
}
