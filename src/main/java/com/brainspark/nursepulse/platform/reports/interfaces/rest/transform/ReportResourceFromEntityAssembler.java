package com.brainspark.nursepulse.platform.reports.interfaces.rest.transform;

import com.brainspark.nursepulse.platform.reports.domain.model.aggregates.Report;
import com.brainspark.nursepulse.platform.reports.interfaces.rest.resources.ReportResource;
import com.brainspark.nursepulse.platform.reports.interfaces.rest.resources.ReportSummaryResource;

public class ReportResourceFromEntityAssembler {

    public static ReportResource toResourceFromEntity(Report entity) {
        var summary = entity.getSummary();
        var summaryResource = summary == null
                ? null
                : new ReportSummaryResource(
                        summary.patients(),
                        summary.vitalSigns(),
                        summary.clinicalEvents(),
                        summary.sbarTransfers(),
                        summary.activeAlerts(),
                        summary.criticalAlerts(),
                        summary.auditLogs()
                );

        return new ReportResource(
                entity.getId(),
                entity.getType().name(),
                entity.getTitle(),
                entity.getGeneratedBy(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getStatus().name(),
                entity.getCreatedAt(),
                summaryResource,
                entity.getClinicalConclusion()
        );
    }
}
