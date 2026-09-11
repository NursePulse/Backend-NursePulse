package com.brainspark.nursepulse.platform.criticalevents.infrastructure.persistence.jpa.assemblers;

import com.brainspark.nursepulse.platform.criticalevents.domain.model.aggregates.Alert;
import com.brainspark.nursepulse.platform.criticalevents.infrastructure.persistence.jpa.entities.AlertPersistenceEntity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

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
                toLocalDateTime(entity.getCreatedAt()),
                entity.getAttendedBy(),
                entity.getAttendedAt(),
                entity.getClosedBy(),
                entity.getResolutionNotes(),
                entity.getClosedAt()
        );
    }

    /**
     * AuditableAbstractPersistenceEntity tracks createdAt as a {@link Date} via
     * Spring Data's {@code @CreatedDate}, populated on every insert since the
     * entity extends it — including rows created before the domain model
     * exposed a dedicated triggeredAt field. Reusing it here means alerts never
     * fall back to a fabricated "now" timestamp on the client.
     */
    private static LocalDateTime toLocalDateTime(Date date) {
        return date == null ? null : date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
