package com.brainspark.nursepulse.platform.handover.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;


@Schema(
        name = "HandoverResponse",
        description = "Handover information response",
        example = "{\"id\": 1, \"patientId\": 1, \"title\": \"Night Shift Handover\", \"description\": \"Pending tasks and key updates for the next shift\", \"status\": \"PENDING\", \"incomingNurseId\": 2, \"additionalNotes\": \"Takes over shift\"}"
)
public record HandoverResource(
        @Schema(description = "Handover unique identifier", example = "1")
        Long id,

        @Schema(description = "Patient ID associated with the handover", example = "1")
        Long patientId,

        @Schema(description = "Handover title", example = "Night Shift Handover")
        String title,

        @Schema(description = "Handover description", example = "Pending tasks and key updates for the next shift")
        String description,

        @Schema(description = "Handover status", example = "PENDING")
        String status,

        @Schema(description = "Nurse ID acknowledging the handover", example = "2")
        Long incomingNurseId,

        @Schema(description = "Optional additional notes", example = "Takes over shift")
        String additionalNotes
) {
}
