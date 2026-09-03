package com.brainspark.nursepulse.platform.criticalevents.domain.model.commands;

public record AttendAlertCommand(
        Long alertId,
        String attendedBy
) {
}
