package com.brainspark.nursepulse.platform.handover.interfaces.rest.transform;

import com.brainspark.nursepulse.platform.handover.domain.model.aggregates.Handover;
import com.brainspark.nursepulse.platform.handover.interfaces.rest.resources.HandoverDetailedResource;

public class HandoverDetailedResourceFromEntityAssembler {

    public static HandoverDetailedResource toResourceFromEntity(Handover entity) {
        return new HandoverDetailedResource(
                entity.getId(),
                entity.getPatientId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getStatus().name(),
                entity.getIncomingNurseId(),
                entity.getAdditionalNotes()
        );
    }
}
