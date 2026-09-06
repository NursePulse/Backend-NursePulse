package com.brainspark.nursepulse.platform.handover.domain.model.commands;

public record CreateHandoverCommand(
        Long patientId,
        String title,
        String situation,
        String background,
        String assessment,
        String recommendation,
        String registeredBy
) {

    public CreateHandoverCommand {
        if (patientId == null) {
            throw new IllegalArgumentException("patientId cannot be null");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title cannot be null or blank");
        }
        if (situation == null || situation.isBlank()) {
            throw new IllegalArgumentException("situation cannot be null or blank");
        }
        if (background == null || background.isBlank()) {
            throw new IllegalArgumentException("background cannot be null or blank");
        }
        if (assessment == null || assessment.isBlank()) {
            throw new IllegalArgumentException("assessment cannot be null or blank");
        }
        if (recommendation == null || recommendation.isBlank()) {
            throw new IllegalArgumentException("recommendation cannot be null or blank");
        }
        if (registeredBy == null || registeredBy.isBlank()) {
            throw new IllegalArgumentException("registeredBy cannot be null or blank");
        }
    }
}
