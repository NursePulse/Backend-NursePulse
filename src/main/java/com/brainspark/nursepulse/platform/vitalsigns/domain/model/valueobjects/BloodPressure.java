package com.brainspark.nursepulse.platform.vitalsigns.domain.model.valueobjects;

import com.brainspark.nursepulse.platform.vitalsigns.domain.exceptions.InvalidVitalSignRecordException;

public record BloodPressure(Integer systolic, Integer diastolic) {
    public BloodPressure {
        if (systolic == null || systolic <= 0) {
            throw new InvalidVitalSignRecordException("Systolic blood pressure must be a positive integer.");
        }
        if (diastolic == null || diastolic <= 0) {
            throw new InvalidVitalSignRecordException("Diastolic blood pressure must be a positive integer.");
        }
        if (systolic <= diastolic) {
            throw new InvalidVitalSignRecordException("Systolic pressure must be greater than diastolic pressure");
        }
    }
}
