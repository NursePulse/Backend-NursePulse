package com.brainspark.nursepulse.platform.vitalsigns.domain.model.queries;

import com.brainspark.nursepulse.platform.vitalsigns.domain.exceptions.InvalidVitalSignRecordException;

public record GetVitalSignRecordsByPatientIdQuery(Long patientId) {
    public GetVitalSignRecordsByPatientIdQuery {
        if (patientId == null || patientId <= 0) {
            throw new InvalidVitalSignRecordException("Patient id must be greater than zero");
        }
    }
}
