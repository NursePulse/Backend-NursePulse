package com.brainspark.nursepulse.platform.auditlogs.infrastructure.pdf;

import com.brainspark.nursepulse.platform.auditlogs.domain.model.aggregates.AuditLog;
import com.brainspark.nursepulse.platform.auditlogs.domain.model.commands.CreateAuditLogCommand;
import com.brainspark.nursepulse.platform.auditlogs.domain.model.valueobjects.AuditActionType;
import com.brainspark.nursepulse.platform.auditlogs.domain.model.valueobjects.AuditedEntityType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AuditLogPdfExportServiceTest {

    private final AuditLogPdfExportService service = new AuditLogPdfExportService();

    @Test
    void shouldRenderNonEmptyPdfForAListOfEntries() {
        var withMetadataDescription = AuditLog.create(new CreateAuditLogCommand(
                10L,
                AuditedEntityType.PATIENT,
                "patient-10",
                AuditActionType.CREATE,
                "nurse.maria",
                null,
                "{\"description\": \"Registro paciente Maria Quispe\"}"
        ));
        var withoutMetadata = AuditLog.create(new CreateAuditLogCommand(
                null,
                AuditedEntityType.AUDIT_LOG,
                "report-1",
                AuditActionType.VIEW,
                "doctor.alejandro",
                null,
                null
        ));

        var pdfBytes = service.render(List.of(withMetadataDescription, withoutMetadata));

        assertTrue(pdfBytes.length > 0);
        assertTrue(pdfBytes[0] == '%' && pdfBytes[1] == 'P' && pdfBytes[2] == 'D' && pdfBytes[3] == 'F');
    }

    @Test
    void shouldRenderAValidPdfEvenWithNoEntries() {
        var pdfBytes = service.render(List.of());

        assertTrue(pdfBytes.length > 0);
    }
}
