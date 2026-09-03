package com.brainspark.nursepulse.platform.handover.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "AcknowledgeHandoverRequest",
        description = "Request payload for acknowledging a handover",
        example = "{\"incomingNurseId\": 2, \"additionalNotes\": \"Patient seems stable, taking over\"}"
)
public record AcknowledgeHandoverResource(
        @Schema(description = "Nurse ID acknowledging the handover", example = "2")
        Long incomingNurseId,

        @Schema(description = "Optional additional notes", example = "Patient seems stable, taking over")
        String additionalNotes
) {
    public AcknowledgeHandoverResource {
        if (incomingNurseId == null) {
            throw new IllegalArgumentException("incomingNurseId cannot be null");
        }
    }
}
