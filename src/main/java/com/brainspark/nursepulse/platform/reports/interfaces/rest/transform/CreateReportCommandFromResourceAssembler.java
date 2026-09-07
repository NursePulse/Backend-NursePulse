package com.brainspark.nursepulse.platform.reports.interfaces.rest.transform;

import com.brainspark.nursepulse.platform.reports.domain.model.commands.CreateReportCommand;
import com.brainspark.nursepulse.platform.reports.domain.model.valueobjects.ReportSummary;
import com.brainspark.nursepulse.platform.reports.interfaces.rest.resources.CreateReportResource;

public class CreateReportCommandFromResourceAssembler {

    public static CreateReportCommand toCommandFromResource(CreateReportResource resource, String generatedBy) {
        var summary = resource.summary();
        return new CreateReportCommand(
                resource.type(),
                resource.title(),
                resource.startDate(),
                resource.endDate(),
                generatedBy,
                new ReportSummary(
                        summary.patients(),
                        summary.vitalSigns(),
                        summary.clinicalEvents(),
                        summary.sbarTransfers(),
                        summary.activeAlerts(),
                        summary.criticalAlerts(),
                        summary.auditLogs()
                ),
                resource.clinicalConclusion()
        );
    }
}
