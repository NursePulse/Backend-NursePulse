package com.brainspark.nursepulse.platform.reports.infrastructure.persistence.jpa.assemblers;

import com.brainspark.nursepulse.platform.reports.domain.model.aggregates.Report;
import com.brainspark.nursepulse.platform.reports.domain.model.commands.CreateReportCommand;
import com.brainspark.nursepulse.platform.reports.domain.model.valueobjects.ReportStatus;
import com.brainspark.nursepulse.platform.reports.domain.model.valueobjects.ReportSummary;
import com.brainspark.nursepulse.platform.reports.domain.model.valueobjects.ReportType;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Covers the domain <-> persistence round trip for Report, in particular the
 * embedded ReportSummary <-> ReportSummaryEmbeddable conversion, which is the
 * newest and most error-prone part of this bounded context (7 plain ints with
 * no type safety tying them to the right column).
 */
class ReportPersistenceAssemblerTest {

    @Test
    void roundTripsAReportWithItsSummaryIntact() {
        var command = new CreateReportCommand(
                ReportType.VITAL_SIGNS,
                "Reporte de signos vitales",
                Instant.parse("2026-09-01T00:00:00Z"),
                Instant.parse("2026-09-07T23:59:59Z"),
                "enfermera.daniela",
                new ReportSummary(2, 8, 1, 3, 2, 1, 15),
                "Se registraron alertas criticas activas."
        );
        var report = new Report(command);

        var entity = ReportPersistenceAssembler.toPersistenceFromDomain(report);
        var rehydrated = ReportPersistenceAssembler.toDomainFromPersistence(entity);

        assertEquals(ReportType.VITAL_SIGNS, rehydrated.getType());
        assertEquals("Reporte de signos vitales", rehydrated.getTitle());
        assertEquals("enfermera.daniela", rehydrated.getGeneratedBy());
        assertEquals(ReportStatus.COMPLETED, rehydrated.getStatus());
        assertEquals("Se registraron alertas criticas activas.", rehydrated.getClinicalConclusion());

        var summary = rehydrated.getSummary();
        assertEquals(2, summary.patients());
        assertEquals(8, summary.vitalSigns());
        assertEquals(1, summary.clinicalEvents());
        assertEquals(3, summary.sbarTransfers());
        assertEquals(2, summary.activeAlerts());
        assertEquals(1, summary.criticalAlerts());
        assertEquals(15, summary.auditLogs());
    }

    @Test
    void returnsNullWhenEntityIsNull() {
        assertNull(ReportPersistenceAssembler.toDomainFromPersistence(null));
    }

    @Test
    void preservesExistingIdWhenConvertingBackToPersistenceForAnUpdate() {
        var command = new CreateReportCommand(
                ReportType.GENERAL,
                "Reporte existente",
                Instant.parse("2026-09-01T00:00:00Z"),
                Instant.parse("2026-09-02T00:00:00Z"),
                "medico.torres",
                new ReportSummary(0, 0, 0, 0, 0, 0, 0),
                null
        );
        var report = new Report(command);
        report.setId(42L);

        var entity = ReportPersistenceAssembler.toPersistenceFromDomain(report);

        assertEquals(42L, entity.getId());
    }
}
