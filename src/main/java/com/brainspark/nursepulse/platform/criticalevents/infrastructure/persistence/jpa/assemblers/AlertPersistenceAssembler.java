package com.brainspark.nursepulse.platform.criticalevents.infrastructure.persistence.jpa.assemblers;

import com.brainspark.nursepulse.platform.criticalevents.domain.model.aggregates.Alert;
import com.brainspark.nursepulse.platform.criticalevents.infrastructure.persistence.jpa.entities.AlertPersistenceEntity;

public final class AlertPersistenceAssembler {

    private AlertPersistenceAssembler() {
    }

    public static AlertPersistenceEntity toPersistenceFromDomain(Alert alert) {
        var entity = new AlertPersistenceEntity();

        entity.setId(alert.getId());
        entity.setPatientId(alert.getPatientId());
        entity.setType(alert.getType());
        entity.setSeverity(alert.getSeverity());
        entity.setDescription(alert.getDescription());
        entity.setStatus(alert.getStatus());
        entity.setTriggeredBy(alert.getTriggeredBy());
        entity.setAttendedBy(alert.getAttendedBy());
        entity.setAttendedAt(alert.getAttendedAt());
        entity.setClosedBy(alert.getClosedBy());
        entity.setResolutionNotes(alert.getResolutionNotes());
        entity.setClosedAt(alert.getClosedAt());

        return entity;
    }

    public static Alert toDomainFromPersistence(AlertPersistenceEntity entity) {
        return Alert.reconstitute(
                entity.getId(),
                entity.getPatientId(),
                entity.getType(),
                entity.getSeverity(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getTriggeredBy(),
                entity.getAttendedBy(),
                entity.getAttendedAt(),
                entity.getClosedBy(),
                entity.getResolutionNotes(),
                entity.getClosedAt()
        );
    }
}
