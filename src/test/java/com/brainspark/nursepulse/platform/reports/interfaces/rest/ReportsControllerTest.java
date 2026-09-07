package com.brainspark.nursepulse.platform.reports.interfaces.rest;

import com.brainspark.nursepulse.platform.reports.application.commandservices.ReportCommandService;
import com.brainspark.nursepulse.platform.reports.application.queryservices.ReportQueryService;
import com.brainspark.nursepulse.platform.reports.domain.model.aggregates.Report;
import com.brainspark.nursepulse.platform.reports.domain.model.commands.CreateReportCommand;
import com.brainspark.nursepulse.platform.reports.domain.model.queries.GetAllReportsQuery;
import com.brainspark.nursepulse.platform.reports.domain.model.valueobjects.ReportStatus;
import com.brainspark.nursepulse.platform.reports.domain.model.valueobjects.ReportType;
import com.brainspark.nursepulse.platform.reports.interfaces.rest.resources.CreateReportResource;
import com.brainspark.nursepulse.platform.reports.interfaces.rest.resources.ReportSummaryResource;
import com.brainspark.nursepulse.platform.shared.application.result.ApplicationError;
import com.brainspark.nursepulse.platform.shared.application.result.Result;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Mirrors the security-focused controller tests already in place for handovers,
 * vital signs and audit logs: the reporting user must always come from the
 * authenticated JWT, never from client-supplied input, since a report's
 * whole point (US-26/TS-07) is persisting who really generated it.
 */
@ExtendWith(MockitoExtension.class)
class ReportsControllerTest {

    @Mock
    private ReportCommandService reportCommandService;

    @Mock
    private ReportQueryService reportQueryService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ReportsController controller;

    @Test
    void shouldUseAuthenticatedUsernameAsGeneratedByInsteadOfClientInput() {
        var resource = new CreateReportResource(
                ReportType.GENERAL,
                "Resumen clinico de la tarde",
                Instant.parse("2026-09-01T00:00:00Z"),
                Instant.parse("2026-09-07T23:59:59Z"),
                new ReportSummaryResource(1, 5, 0, 1, 0, 0, 12),
                "Periodo sin alertas criticas activas."
        );
        when(authentication.getName()).thenReturn("medico.torres");
        when(reportCommandService.handle(any(CreateReportCommand.class))).thenReturn(
                Result.failure(ApplicationError.unexpected("test", "stop after capture"))
        );

        controller.createReport(resource, authentication);

        var captor = ArgumentCaptor.forClass(CreateReportCommand.class);
        verify(reportCommandService).handle(captor.capture());
        assertEquals("medico.torres", captor.getValue().generatedBy());
    }

    @Test
    void shouldReturnAllReportsFromQueryService() {
        var report = new Report(new CreateReportCommand(
                ReportType.GENERAL,
                "Reporte de prueba",
                Instant.parse("2026-09-01T00:00:00Z"),
                Instant.parse("2026-09-07T23:59:59Z"),
                "medico.torres",
                new com.brainspark.nursepulse.platform.reports.domain.model.valueobjects.ReportSummary(
                        1, 5, 0, 1, 0, 0, 12
                ),
                "Conclusion de prueba."
        ));
        when(reportQueryService.handle(any(GetAllReportsQuery.class))).thenReturn(List.of(report));

        var response = controller.getAllReports();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(ReportStatus.COMPLETED, report.getStatus());
    }
}
