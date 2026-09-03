package com.brainspark.nursepulse.platform.criticalevents.interfaces.rest.resources;

import com.brainspark.nursepulse.platform.criticalevents.domain.model.valueobjects.AlertSeverity;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.valueobjects.AlertType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateAlertResource(
        @NotNull
        @Positive
        Long patientId,

        @NotNull
        AlertType type,

        @NotNull
        AlertSeverity severity,

        @NotBlank
        @Size(max = 255)
        String description,

        @NotBlank
        @Size(max = 120)
        String triggeredBy
) {
}
