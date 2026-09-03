package com.brainspark.nursepulse.platform.criticalevents.domain.model.commands;

public record CloseAlertCommand(
        Long alertId,
        String closedBy,
        String resolutionNotes
) {
}
