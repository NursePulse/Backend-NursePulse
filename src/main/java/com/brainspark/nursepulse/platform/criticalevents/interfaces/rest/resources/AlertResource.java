package com.brainspark.nursepulse.platform.criticalevents.interfaces.rest.resources;

import com.brainspark.nursepulse.platform.criticalevents.domain.model.valueobjects.AlertSeverity;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.valueobjects.AlertStatus;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.valueobjects.AlertType;

import java.time.LocalDateTime;

public record AlertResource(
        Long id,
        Long patientId,
        AlertType type,
        AlertSeverity severity,
        String description,
        AlertStatus status,
        String triggeredBy,
        String attendedBy,
        LocalDateTime attendedAt,
        String closedBy,
        String resolutionNotes,
        LocalDateTime closedAt
) {
}
