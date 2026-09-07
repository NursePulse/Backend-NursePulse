package com.brainspark.nursepulse.platform.reports.domain.model.commands;

import com.brainspark.nursepulse.platform.reports.domain.model.valueobjects.ReportSummary;
import com.brainspark.nursepulse.platform.reports.domain.model.valueobjects.ReportType;

import java.time.Instant;

public record CreateReportCommand(
        ReportType type,
        String title,
        Instant startDate,
        Instant endDate,
        String generatedBy,
        ReportSummary summary,
        String clinicalConclusion
) {
    public CreateReportCommand {
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title cannot be null or blank");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("startDate cannot be null");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("endDate cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate cannot be after endDate");
        }
        if (generatedBy == null || generatedBy.isBlank()) {
            throw new IllegalArgumentException("generatedBy cannot be null or blank");
        }
        if (summary == null) {
            throw new IllegalArgumentException("summary cannot be null");
        }
    }
}
