package com.brainspark.nursepulse.platform.reports.interfaces.rest.resources;

import com.brainspark.nursepulse.platform.reports.domain.model.valueobjects.ReportType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

@Schema(
        name = "CreateReportRequest",
        description = "Request payload for persisting a consolidated clinical report. " +
                "The summary counts are computed by the client against the real, live clinical " +
                "endpoints for the requested period; this endpoint only persists the result so it " +
                "can be consulted later by any authorized user, instead of staying local to one browser."
)
public record CreateReportResource(
        @NotNull
        @Schema(description = "Report category", example = "GENERAL")
        ReportType type,

        @NotBlank(message = "title is required")
        @Schema(description = "Report title", example = "Resumen clinico de la tarde")
        String title,

        @NotNull
        @Schema(description = "Inclusive lower bound of the reported period", example = "2026-09-01T00:00:00Z")
        Instant startDate,

        @NotNull
        @Schema(description = "Inclusive upper bound of the reported period", example = "2026-09-07T23:59:59Z")
        Instant endDate,

        @Valid
        @NotNull(message = "summary is required")
        ReportSummaryResource summary,

        @Nullable
        @Schema(description = "Optional clinical conclusion/narrative for the period")
        String clinicalConclusion
) {
}
