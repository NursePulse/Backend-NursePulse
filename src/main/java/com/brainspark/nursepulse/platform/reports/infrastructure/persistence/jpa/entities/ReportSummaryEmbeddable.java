package com.brainspark.nursepulse.platform.reports.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportSummaryEmbeddable {

    @Column(name = "summary_patients", nullable = false)
    private int patients;

    @Column(name = "summary_vital_signs", nullable = false)
    private int vitalSigns;

    @Column(name = "summary_clinical_events", nullable = false)
    private int clinicalEvents;

    @Column(name = "summary_sbar_transfers", nullable = false)
    private int sbarTransfers;

    @Column(name = "summary_active_alerts", nullable = false)
    private int activeAlerts;

    @Column(name = "summary_critical_alerts", nullable = false)
    private int criticalAlerts;

    @Column(name = "summary_audit_logs", nullable = false)
    private int auditLogs;
}
