package com.brainspark.nursepulse.platform.handover.interfaces.rest.transform;

import com.brainspark.nursepulse.platform.handover.domain.model.aggregates.Handover;
import com.brainspark.nursepulse.platform.handover.interfaces.rest.resources.HandoverResource;


public class HandoverResourceFromEntityAssembler {

    public static HandoverResource toResourceFromEntity(Handover entity) {
        return new HandoverResource(
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
                entity.getTargetNurseId(),
                entity.getStatus().name(),
                entity.getIncomingNurseId(),
                entity.getAdditionalNotes());
    }
}
