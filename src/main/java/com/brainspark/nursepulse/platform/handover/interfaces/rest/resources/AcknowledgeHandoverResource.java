package com.brainspark.nursepulse.platform.handover.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "AcknowledgeHandoverRequest",
        description = "Request payload for acknowledging a handover. The acknowledging nurse is derived from the authenticated JWT, not from this payload.",
        example = "{\"additionalNotes\": \"Patient seems stable, taking over\"}"
)
public record AcknowledgeHandoverResource(
        @Schema(description = "Optional additional notes", example = "Patient seems stable, taking over")
        String additionalNotes
) {
}
