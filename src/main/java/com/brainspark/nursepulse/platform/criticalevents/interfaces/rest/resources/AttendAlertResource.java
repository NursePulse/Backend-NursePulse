package com.brainspark.nursepulse.platform.criticalevents.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AttendAlertResource(
        @NotBlank
        @Size(max = 120)
        String attendedBy
) {
}
