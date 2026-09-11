package com.brainspark.nursepulse.platform.handover.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;


@Schema(
        name = "CreateHandoverRequest",
        description = "Request payload for creating a new SBAR handover",
        example = "{\"patientId\": 1, \"title\": \"Night Shift Handover\", " +
                "\"situation\": \"Patient stable, mild chest discomfort\", " +
                "\"background\": \"Admitted for unstable angina, day 2\", " +
                "\"assessment\": \"Vitals within range, pain controlled\", " +
                "\"recommendation\": \"Continue monitoring, next troponin at 06:00\", " +
                "\"targetNurseId\": 2}"
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
                description = "SBAR - Situation",
                example = "Patient stable, mild chest discomfort",
                minLength = 1,
                maxLength = 1000
        )
        String situation,

        @Schema(
                description = "SBAR - Background",
                example = "Admitted for unstable angina, day 2",
                minLength = 1,
                maxLength = 1000
        )
        String background,

        @Schema(
                description = "SBAR - Assessment",
                example = "Vitals within range, pain controlled",
                minLength = 1,
                maxLength = 1000
        )
        String assessment,

        @Schema(
                description = "SBAR - Recommendation",
                example = "Continue monitoring, next troponin at 06:00",
                minLength = 1,
                maxLength = 1000
        )
        String recommendation,

        @Schema(
                description = "Optional identifier of the nurse this handover is intended for. Not the acknowledging nurse — that is derived from the JWT when the handover is acknowledged.",
                example = "2"
        )
        Long targetNurseId
) {

    public CreateHandoverResource {
        if (patientId == null) {
            throw new IllegalArgumentException("Patient ID is required");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (situation == null || situation.isBlank()) {
            throw new IllegalArgumentException("Situation is required");
        }
        if (background == null || background.isBlank()) {
            throw new IllegalArgumentException("Background is required");
        }
        if (assessment == null || assessment.isBlank()) {
            throw new IllegalArgumentException("Assessment is required");
        }
        if (recommendation == null || recommendation.isBlank()) {
            throw new IllegalArgumentException("Recommendation is required");
        }
    }
}
