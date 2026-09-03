package com.brainspark.nursepulse.platform.clinicalevents.infrastructure.persistence.jpa.assemblers;

import com.brainspark.nursepulse.platform.clinicalevents.domain.model.aggregates.ClinicalEvent;
import com.brainspark.nursepulse.platform.clinicalevents.infrastructure.persistence.jpa.entities.ClinicalEventPersistenceEntity;

public final class ClinicalEventPersistenceAssembler {

    private ClinicalEventPersistenceAssembler() {
    }

    public static ClinicalEvent toDomainFromPersistence(ClinicalEventPersistenceEntity entity) {
        if (entity == null) return null;

        var clinicalEvent = new ClinicalEvent();
        clinicalEvent.setId(entity.getId());
        clinicalEvent.setPatientId(entity.getPatientId());
        clinicalEvent.setEventType(entity.getEventType());
        clinicalEvent.setSeverity(entity.getSeverity());
        clinicalEvent.setTitle(entity.getTitle());
        clinicalEvent.setDescription(entity.getDescription());
        clinicalEvent.setRegisteredBy(entity.getRegisteredBy());
        clinicalEvent.setOccurredAt(entity.getOccurredAt());
        return clinicalEvent;
    }

    public static ClinicalEventPersistenceEntity toPersistenceFromDomain(ClinicalEvent clinicalEvent) {
        if (clinicalEvent == null) return null;

        var entity = new ClinicalEventPersistenceEntity();
        // Only set ID for updates; new events let JPA generate it.
        if (clinicalEvent.getId() != null) {
            entity.setId(clinicalEvent.getId());
        }
        entity.setPatientId(clinicalEvent.getPatientId());
        entity.setEventType(clinicalEvent.getEventType());
        entity.setSeverity(clinicalEvent.getSeverity());
        entity.setTitle(clinicalEvent.getTitle());
        entity.setDescription(clinicalEvent.getDescription());
        entity.setRegisteredBy(clinicalEvent.getRegisteredBy());
        entity.setOccurredAt(clinicalEvent.getOccurredAt());
        return entity;
    }
}
