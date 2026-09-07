package com.brainspark.nursepulse.platform.reports.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Consolidated clinical activity counts for the reported period")
public record ReportSummaryResource(
        @NotNull @Min(0) Integer patients,
        @NotNull @Min(0) Integer vitalSigns,
        @NotNull @Min(0) Integer clinicalEvents,
        @NotNull @Min(0) Integer sbarTransfers,
        @NotNull @Min(0) Integer activeAlerts,
        @NotNull @Min(0) Integer criticalAlerts,
        @NotNull @Min(0) Integer auditLogs
) {
}
