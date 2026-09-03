package com.brainspark.nursepulse.platform.vitalsigns.interfaces.rest.resources;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateVitalSignRecordResource(
        @NotNull Long patientId,
        @NotNull Long nurseId,

        @NotNull
        @Min(20)
        @Max(250)
        Integer heartRate,

        @NotNull
        @Min(5)
        @Max(80)
        Integer respiratoryRate,

        @NotNull
        @Min(50)
        @Max(260)
        Integer systolicPressure,

        @NotNull
        @Min(30)
        @Max(180)
        Integer diastolicPressure,

        @NotNull
        @Min(0)
        @Max(100)
        Integer oxygenSaturation,

        @NotNull
        @DecimalMin("30.0")
        @DecimalMax("45.0")
        BigDecimal temperature,

        LocalDateTime recordedAt
) {
}