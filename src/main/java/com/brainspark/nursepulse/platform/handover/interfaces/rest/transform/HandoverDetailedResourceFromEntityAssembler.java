package com.brainspark.nursepulse.platform.handover.interfaces.rest.transform;

import com.brainspark.nursepulse.platform.handover.domain.model.aggregates.Handover;
import com.brainspark.nursepulse.platform.handover.interfaces.rest.resources.HandoverDetailedResource;

public class HandoverDetailedResourceFromEntityAssembler {

    public static HandoverDetailedResource toResourceFromEntity(Handover entity) {
        return new HandoverDetailedResource(
                entity.getId(),
                entity.getCreatedAt(),
                entity.getPatientId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getSituation(),
                entity.getBackground(),
                entity.getAssessment(),
                entity.getRecommendation(),
                entity.getRegisteredBy(),
                entity.getStatus().name(),
                entity.getIncomingNurseId(),
                entity.getAdditionalNotes()
        );
    }
}
