package com.brainspark.nursepulse.platform.handover.domain.model.queries;

public record GetHandoverByIdQuery(Long handoverId) {

    public GetHandoverByIdQuery {
        if (handoverId == null || handoverId <= 0) throw new IllegalArgumentException("Handover id is required.");
    }
}
