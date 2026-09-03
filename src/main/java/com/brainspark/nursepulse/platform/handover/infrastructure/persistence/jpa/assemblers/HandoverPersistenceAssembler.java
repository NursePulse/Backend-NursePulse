package com.brainspark.nursepulse.platform.handover.infrastructure.persistence.jpa.assemblers;

import com.brainspark.nursepulse.platform.handover.domain.model.aggregates.Handover;
import com.brainspark.nursepulse.platform.handover.infrastructure.persistence.jpa.entities.HandoverPersistenceEntity;


public final class HandoverPersistenceAssembler {

    private HandoverPersistenceAssembler() {
    }

    public static Handover toDomainFromPersistence(HandoverPersistenceEntity entity) {
        if (entity == null) return null;

        var handover = new Handover();
        handover.setId(entity.getId());
        handover.setPatientId(entity.getPatientId());
        handover.setTitle(entity.getTitle());
        handover.setDescription(entity.getDescription());
        handover.setStatus(entity.getStatus());
        handover.setIncomingNurseId(entity.getIncomingNurseId());
        handover.setAdditionalNotes(entity.getAdditionalNotes());
        return handover;
    }

    public static HandoverPersistenceEntity toPersistenceFromDomain(Handover handover) {
        if (handover == null) return null;

        var entity = new HandoverPersistenceEntity();
        // Only set ID if the handover is being updated (has a non-null ID)
        // For new handovers, leave ID null to allow JPA to generate it
        if (handover.getId() != null) {
            entity.setId(handover.getId());
        }
        entity.setPatientId(handover.getPatientId());
        entity.setTitle(handover.getTitle());
        entity.setDescription(handover.getDescription());
        entity.setStatus(handover.getStatus());
        entity.setIncomingNurseId(handover.getIncomingNurseId());
        entity.setAdditionalNotes(handover.getAdditionalNotes());
        return entity;
    }
}
