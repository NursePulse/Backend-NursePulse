package com.brainspark.nursepulse.platform.vitalsigns.domain.model.aggregates;

import com.brainspark.nursepulse.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.brainspark.nursepulse.platform.vitalsigns.domain.exceptions.InvalidVitalSignRecordException;
import com.brainspark.nursepulse.platform.vitalsigns.domain.model.commands.CreateVitalSignRecordCommand;
import com.brainspark.nursepulse.platform.vitalsigns.domain.model.events.VitalSignRecordedEvent;
import com.brainspark.nursepulse.platform.vitalsigns.domain.model.valueobjects.BloodPressure;
import com.brainspark.nursepulse.platform.vitalsigns.domain.model.valueobjects.RiskLevel;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class VitalSignRecord extends AbstractDomainAggregateRoot<VitalSignRecord> {

    @Setter
    private Long id;

    private Long patientId;
    private Long nurseId;
    private Integer heartRate;
    private Integer respiratoryRate;
    private BloodPressure bloodPressure;
    private Integer oxygenSaturation;
    private BigDecimal temperature;
    private RiskLevel riskLevel;
    private LocalDateTime recordedAt;

    public VitalSignRecord() {
        this.riskLevel = RiskLevel.UNASSESSED;
        this.recordedAt = LocalDateTime.now();
    }

    private VitalSignRecord(
            Long id,
            Long patientId,
            Long nurseId,
            Integer heartRate,
            Integer respiratoryRate,
            BloodPressure bloodPressure,
            Integer oxygenSaturation,
            BigDecimal temperature,
            RiskLevel riskLevel,
            LocalDateTime recordedAt
    ) {
        this.id = id;
        this.patientId = patientId;
        this.nurseId = nurseId;
        this.heartRate = heartRate;
        this.respiratoryRate = respiratoryRate;
        this.bloodPressure = bloodPressure;
        this.oxygenSaturation = oxygenSaturation;
        this.temperature = temperature;
        this.riskLevel = riskLevel;
        this.recordedAt = recordedAt;
    }

    public VitalSignRecord(CreateVitalSignRecordCommand command) {
        this.patientId = command.patientId();
        this.nurseId = command.nurseId();
        this.heartRate = command.heartRate();
        this.respiratoryRate = command.respiratoryRate();
        this.bloodPressure = command.bloodPressure();
        this.oxygenSaturation = command.oxygenSaturation();
        this.temperature = command.temperature();
        this.riskLevel = RiskLevel.UNASSESSED;
        this.recordedAt = command.recordedAt() != null
                ? command.recordedAt()
                : LocalDateTime.now();

        this.registerDomainEvent(new VitalSignRecordedEvent(
                this.patientId,
                this.nurseId,
                this.recordedAt
        ));
    }

    public VitalSignRecord assignRiskLevel(RiskLevel riskLevel) {
        if (riskLevel == null) {
            throw new InvalidVitalSignRecordException("Risk level is required");
        }

        this.riskLevel = riskLevel;
        return this;
    }

    public static VitalSignRecord reconstitute(
            Long id,
            Long patientId,
            Long nurseId,
            Integer heartRate,
            Integer respiratoryRate,
            BloodPressure bloodPressure,
            Integer oxygenSaturation,
            BigDecimal temperature,
            RiskLevel riskLevel,
            LocalDateTime recordedAt
    ) {
        return new VitalSignRecord(
                id,
                patientId,
                nurseId,
                heartRate,
                respiratoryRate,
                bloodPressure,
                oxygenSaturation,
                temperature,
                riskLevel,
                recordedAt
        );
    }
}