package com.brainspark.nursepulse.platform.reports.domain.model.valueobjects;

/**
 * Snapshot of clinical activity counts consolidated at report-generation time.
 * The counting itself happens client-side against the real, live endpoints
 * (patients, vital signs, SBAR handovers, alerts, audit logs); this value
 * object only carries the resulting totals for persistence.
 */
public record ReportSummary(
        int patients,
        int vitalSigns,
        int clinicalEvents,
        int sbarTransfers,
        int activeAlerts,
        int criticalAlerts,
        int auditLogs
) {
}
