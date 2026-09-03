package com.brainspark.nursepulse.platform.handover.domain.model.commands;

public record CreateHandoverCommand(Long patientId, String title, String description) {

    public CreateHandoverCommand {
        if (patientId == null) {
            throw new IllegalArgumentException("patientId cannot be null");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title cannot be null or blank");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("description cannot be null or blank");
        }
    }
}
