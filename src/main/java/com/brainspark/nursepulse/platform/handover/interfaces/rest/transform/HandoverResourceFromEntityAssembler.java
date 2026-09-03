package com.brainspark.nursepulse.platform.handover.interfaces.rest.transform;

import com.brainspark.nursepulse.platform.handover.domain.model.aggregates.Handover;
import com.brainspark.nursepulse.platform.handover.interfaces.rest.resources.HandoverResource;


public class HandoverResourceFromEntityAssembler {

    public static HandoverResource toResourceFromEntity(Handover entity) {
        return new HandoverResource(
                entity.getId(),
                entity.getPatientId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getStatus().name(),
                entity.getIncomingNurseId(),
                entity.getAdditionalNotes());
    }
}
