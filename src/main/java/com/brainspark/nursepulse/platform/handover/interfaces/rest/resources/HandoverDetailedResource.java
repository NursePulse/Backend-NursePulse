package com.brainspark.nursepulse.platform.handover.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "HandoverDetailedResponse",
        description = "Detailed handover information response, including complete SBAR content if available",
        example = "{\"id\": 1, \"patientId\": 1, \"title\": \"Night Shift Handover\", \"description\": \"Situation: Patient stable... Background: admitted for... Assessment: ... Recommendation: ...\", \"status\": \"PENDING\", \"incomingNurseId\": 2, \"additionalNotes\": \"Notes\"}"
)
public record HandoverDetailedResource(
        @Schema(description = "Handover unique identifier", example = "1")
        Long id,

        @Schema(description = "Patient ID associated with the handover", example = "1")
        Long patientId,

        @Schema(description = "Handover title", example = "Night Shift Handover")
        String title,

        @Schema(description = "Handover description (Complete SBAR report)", example = "Situation: Patient stable... Background: admitted for... Assessment: ... Recommendation: ...")
        String description,

        @Schema(description = "Handover status", example = "PENDING")
        String status,

        @Schema(description = "Nurse ID acknowledging the handover", example = "2")
        Long incomingNurseId,

        @Schema(description = "Optional additional notes", example = "Notes")
        String additionalNotes
) {
}
