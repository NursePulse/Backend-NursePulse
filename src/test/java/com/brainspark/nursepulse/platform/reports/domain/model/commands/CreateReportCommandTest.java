package com.brainspark.nursepulse.platform.reports.domain.model.commands;

import com.brainspark.nursepulse.platform.reports.domain.model.valueobjects.ReportSummary;
import com.brainspark.nursepulse.platform.reports.domain.model.valueobjects.ReportType;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateReportCommandTest {

    private static final ReportSummary VALID_SUMMARY = new ReportSummary(1, 1, 0, 0, 0, 0, 1);

    @Test
    void shouldRejectBlankTitle() {
        assertThrows(IllegalArgumentException.class, () -> new CreateReportCommand(
                ReportType.GENERAL,
                " ",
                Instant.parse("2026-09-01T00:00:00Z"),
                Instant.parse("2026-09-02T00:00:00Z"),
                "jefe.torres",
                VALID_SUMMARY,
                null
        ));
    }

    @Test
    void shouldRejectStartDateAfterEndDate() {
        assertThrows(IllegalArgumentException.class, () -> new CreateReportCommand(
                ReportType.GENERAL,
                "Reporte invalido",
                Instant.parse("2026-09-07T00:00:00Z"),
                Instant.parse("2026-09-01T00:00:00Z"),
                "jefe.torres",
                VALID_SUMMARY,
                null
        ));
    }

    @Test
    void shouldRejectBlankGeneratedBy() {
        assertThrows(IllegalArgumentException.class, () -> new CreateReportCommand(
                ReportType.GENERAL,
                "Reporte",
                Instant.parse("2026-09-01T00:00:00Z"),
                Instant.parse("2026-09-02T00:00:00Z"),
                " ",
                VALID_SUMMARY,
                null
        ));
    }

    @Test
    void shouldRejectNullSummary() {
        assertThrows(IllegalArgumentException.class, () -> new CreateReportCommand(
                ReportType.GENERAL,
                "Reporte",
                Instant.parse("2026-09-01T00:00:00Z"),
                Instant.parse("2026-09-02T00:00:00Z"),
                "jefe.torres",
                null,
                null
        ));
    }

    @Test
    void shouldAcceptAValidCommandWithoutAClinicalConclusion() {
        assertDoesNotThrow(() -> new CreateReportCommand(
                ReportType.GENERAL,
                "Reporte valido",
                Instant.parse("2026-09-01T00:00:00Z"),
                Instant.parse("2026-09-02T00:00:00Z"),
                "jefe.torres",
                VALID_SUMMARY,
                null
        ));
    }
}
