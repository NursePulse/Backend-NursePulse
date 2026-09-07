package com.brainspark.nursepulse.platform.reports.infrastructure.persistence.jpa.assemblers;

import com.brainspark.nursepulse.platform.reports.domain.model.aggregates.Report;
import com.brainspark.nursepulse.platform.reports.domain.model.valueobjects.ReportSummary;
import com.brainspark.nursepulse.platform.reports.infrastructure.persistence.jpa.entities.ReportPersistenceEntity;
import com.brainspark.nursepulse.platform.reports.infrastructure.persistence.jpa.entities.ReportSummaryEmbeddable;

public final class ReportPersistenceAssembler {

    private ReportPersistenceAssembler() {
    }

    public static Report toDomainFromPersistence(ReportPersistenceEntity entity) {
        if (entity == null) return null;

        var report = new Report();
        report.setId(entity.getId());
        report.setCreatedAt(entity.getCreatedAt());
        report.setType(entity.getType());
        report.setTitle(entity.getTitle());
        report.setGeneratedBy(entity.getGeneratedBy());
        report.setStartDate(entity.getStartDate());
        report.setEndDate(entity.getEndDate());
        report.setStatus(entity.getStatus());
        report.setClinicalConclusion(entity.getClinicalConclusion());

        var summary = entity.getSummary();
        if (summary != null) {
            report.setSummary(new ReportSummary(
                    summary.getPatients(),
                    summary.getVitalSigns(),
                    summary.getClinicalEvents(),
                    summary.getSbarTransfers(),
                    summary.getActiveAlerts(),
                    summary.getCriticalAlerts(),
                    summary.getAuditLogs()
            ));
        }

        return report;
    }

    public static ReportPersistenceEntity toPersistenceFromDomain(Report report) {
        if (report == null) return null;

        var entity = new ReportPersistenceEntity();
        if (report.getId() != null) {
            entity.setId(report.getId());
        }
        entity.setType(report.getType());
        entity.setTitle(report.getTitle());
        entity.setGeneratedBy(report.getGeneratedBy());
        entity.setStartDate(report.getStartDate());
        entity.setEndDate(report.getEndDate());
        entity.setStatus(report.getStatus());
        entity.setClinicalConclusion(report.getClinicalConclusion());

        var summary = report.getSummary();
        if (summary != null) {
            entity.setSummary(new ReportSummaryEmbeddable(
                    summary.patients(),
                    summary.vitalSigns(),
                    summary.clinicalEvents(),
                    summary.sbarTransfers(),
                    summary.activeAlerts(),
                    summary.criticalAlerts(),
                    summary.auditLogs()
            ));
        }

        return entity;
    }
}
