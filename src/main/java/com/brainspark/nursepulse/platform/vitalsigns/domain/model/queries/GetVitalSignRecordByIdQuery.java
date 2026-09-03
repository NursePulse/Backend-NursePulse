package com.brainspark.nursepulse.platform.vitalsigns.domain.model.queries;

import com.brainspark.nursepulse.platform.vitalsigns.domain.exceptions.InvalidVitalSignRecordException;

public record GetVitalSignRecordByIdQuery(Long vitalSignRecordId) {

    public GetVitalSignRecordByIdQuery {
        if (vitalSignRecordId == null || vitalSignRecordId <= 0) {
            throw new InvalidVitalSignRecordException("Vital sign record id must be greater than zero");
        }
    }
}