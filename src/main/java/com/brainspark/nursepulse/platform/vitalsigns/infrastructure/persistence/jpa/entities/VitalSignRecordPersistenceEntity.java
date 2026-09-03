package com.brainspark.nursepulse.platform.vitalsigns.infrastructure.persistence.jpa.entities;

import com.brainspark.nursepulse.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import com.brainspark.nursepulse.platform.vitalsigns.domain.model.valueobjects.RiskLevel;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vital_sign_records")
@Getter
@Setter
@NoArgsConstructor
public class VitalSignRecordPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false)
    private Long nurseId;

    @Column(nullable = false)
    private Integer heartRate;

    @Column(nullable = false)
    private Integer respiratoryRate;

    @Embedded
    private BloodPressurePersistenceEmbeddable bloodPressure;

    @Column(nullable = false)
    private Integer oxygenSaturation;

    @Column(nullable = false, precision = 4, scale = 1)
    private BigDecimal temperature;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RiskLevel riskLevel;

    @Column(nullable = false)
    private LocalDateTime recordedAt;
}