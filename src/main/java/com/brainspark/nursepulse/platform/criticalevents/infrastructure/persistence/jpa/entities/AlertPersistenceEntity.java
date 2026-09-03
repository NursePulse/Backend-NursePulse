package com.brainspark.nursepulse.platform.criticalevents.infrastructure.persistence.jpa.entities;

import com.brainspark.nursepulse.platform.criticalevents.domain.model.valueobjects.AlertSeverity;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.valueobjects.AlertStatus;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.valueobjects.AlertType;
import com.brainspark.nursepulse.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
@Getter
@Setter
@NoArgsConstructor
public class AlertPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(nullable = false)
    private Long patientId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AlertType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AlertSeverity severity;

    @Column(nullable = false, length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AlertStatus status;

    @Column(nullable = false, length = 120)
    private String triggeredBy;

    @Column(length = 120)
    private String attendedBy;

    private LocalDateTime attendedAt;

    @Column(length = 120)
    private String closedBy;

    @Column(length = 255)
    private String resolutionNotes;

    private LocalDateTime closedAt;
}
