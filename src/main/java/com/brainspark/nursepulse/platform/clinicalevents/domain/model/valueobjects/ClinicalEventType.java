package com.brainspark.nursepulse.platform.clinicalevents.domain.model.valueobjects;

/**
 * Types of clinical events registered during a shift.
 */
public enum ClinicalEventType {
    MEDICATION,
    PROCEDURE,
    CONDITION_CHANGE,
    COMPLICATION,
    EMERGENCY,
    OBSERVATION
}
