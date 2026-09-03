package com.brainspark.nursepulse.platform.auditlogs.domain.model.valueobjects;

/**
 * Represents the type of clinical action that triggered the audit log entry.
 */
public enum AuditActionType {
    CREATE,
    UPDATE,
    DELETE,
    VIEW,
    SIGN,
    HANDOVER,
    ALERT_TRIGGERED,
    ALERT_ACKNOWLEDGED,
    VITAL_SIGNS_RECORDED,
    CLINICAL_NOTE_ADDED
}