package com.brainspark.nursepulse.platform.handover.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;


@Schema(
        name = "HandoverResponse",
        description = "Handover information response",
        example = "{\"id\": 1, \"patientId\": 1, \"title\": \"Night Shift Handover\", \"description\": \"Pending tasks and key updates for the next shift\", \"situation\": \"...\", \"background\": \"...\", \"assessment\": \"...\", \"recommendation\": \"...\", \"registeredBy\": \"nurse.daniela\", \"status\": \"PENDING\", \"incomingNurseId\": 2, \"additionalNotes\": \"Takes over shift\"}"
)
public record HandoverResource(
        @Schema(description = "Handover unique identifier", example = "1")
        Long id,

        @Schema(description = "Timestamp when the handover was registered")
        Date createdAt,

        @Schema(description = "Patient ID associated with the handover", example = "1")
        Long patientId,

        @Schema(description = "Handover title", example = "Night Shift Handover")
        String title,

        @Schema(description = "Handover description", example = "Pending tasks and key updates for the next shift")
        String description,

        @Schema(description = "SBAR - Situation")
        String situation,

        @Schema(description = "SBAR - Background")
        String background,

        @Schema(description = "SBAR - Assessment")
        String assessment,

        @Schema(description = "SBAR - Recommendation")
        String recommendation,

        @Schema(description = "Username of the nurse who registered the handover", example = "nurse.daniela")
        String registeredBy,

        @Schema(description = "Handover status", example = "PENDING")
        String status,

        @Schema(description = "Nurse ID acknowledging the handover", example = "2")
        Long incomingNurseId,

        @Schema(description = "Optional additional notes", example = "Takes over shift")
        String additionalNotes
) {
}
