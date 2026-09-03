package com.brainspark.nursepulse.platform.handover.domain.model.queries;

import java.util.Date;

public record GetAllHandoversByPatientIdQuery(Long patientId, Date startDate, Date endDate) {
    public GetAllHandoversByPatientIdQuery {
        if (patientId == null) {
            throw new IllegalArgumentException("patientId cannot be null");
        }
    }
}
