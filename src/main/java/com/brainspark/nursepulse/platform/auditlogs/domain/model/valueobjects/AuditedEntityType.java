package com.brainspark.nursepulse.platform.auditlogs.domain.model.valueobjects;

/**
 * Identifies the type of clinical entity on which the audited action was performed.
 */
public enum AuditedEntityType {
    PATIENT,
    VITAL_SIGNS,
    SBAR_HANDOVER,
    CLINICAL_EVENT,
    ALERT,
    MEDICATION_ORDER,
    CARE_PLAN,
    USER,
    AUDIT_LOG
}