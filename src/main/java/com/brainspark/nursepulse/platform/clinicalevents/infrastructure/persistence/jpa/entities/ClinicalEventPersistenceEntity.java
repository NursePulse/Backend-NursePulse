package com.brainspark.nursepulse.platform.clinicalevents.infrastructure.persistence.jpa.entities;

import com.brainspark.nursepulse.platform.clinicalevents.domain.model.valueobjects.ClinicalEventSeverity;
import com.brainspark.nursepulse.platform.clinicalevents.domain.model.valueobjects.ClinicalEventType;
import com.brainspark.nursepulse.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "clinical_events")
@Getter
@Setter
@NoArgsConstructor
public class ClinicalEventPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private ClinicalEventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClinicalEventSeverity severity;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(name = "registered_by", nullable = false)
    private String registeredBy;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;
}
