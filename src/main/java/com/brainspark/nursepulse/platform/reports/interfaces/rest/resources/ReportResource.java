package com.brainspark.nursepulse.platform.reports.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Date;

@Schema(name = "ReportResponse", description = "Persisted clinical report")
public record ReportResource(
        @Schema(description = "Report unique identifier", example = "1")
        Long id,

        @Schema(description = "Report category", example = "GENERAL")
        String type,

        @Schema(description = "Report title", example = "Resumen clinico de la tarde")
        String title,

        @Schema(description = "Username of the staff member who generated the report", example = "jefe.torres")
        String generatedBy,

        @Schema(description = "Inclusive lower bound of the reported period")
        Instant startDate,

        @Schema(description = "Inclusive upper bound of the reported period")
        Instant endDate,

        @Schema(description = "Report status", example = "COMPLETED")
        String status,

        @Schema(description = "Timestamp when the report was persisted")
        Date createdAt,

        ReportSummaryResource summary,

        @Schema(description = "Optional clinical conclusion/narrative for the period")
        String clinicalConclusion
) {
}
