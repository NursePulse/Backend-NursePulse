package com.brainspark.nursepulse.platform.criticalevents.domain.model.commands;

import com.brainspark.nursepulse.platform.criticalevents.domain.model.valueobjects.AlertSeverity;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.valueobjects.AlertType;

public record CreateAlertCommand(
        Long patientId,
        AlertType type,
        AlertSeverity severity,
        String description,
        String triggeredBy
) {
}
