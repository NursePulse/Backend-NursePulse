package com.brainspark.nursepulse.platform.criticalevents.domain.model.queries;

import com.brainspark.nursepulse.platform.criticalevents.domain.exceptions.InvalidAlertException;

public record GetAlertsByPatientIdQuery(Long patientId) {

    public GetAlertsByPatientIdQuery {
        if (patientId == null || patientId <= 0) {
            throw new InvalidAlertException("Patient id must be greater than zero");
        }
    }
}
