package com.brainspark.nursepulse.platform.vitalsigns.infrastructure.persistence.jpa.assemblers;

import com.brainspark.nursepulse.platform.vitalsigns.domain.model.aggregates.VitalSignRecord;
import com.brainspark.nursepulse.platform.vitalsigns.domain.model.valueobjects.BloodPressure;
import com.brainspark.nursepulse.platform.vitalsigns.infrastructure.persistence.jpa.entities.BloodPressurePersistenceEmbeddable;
import com.brainspark.nursepulse.platform.vitalsigns.infrastructure.persistence.jpa.entities.VitalSignRecordPersistenceEntity;

public final class VitalSignRecordPersistenceAssembler {

    private VitalSignRecordPersistenceAssembler() {
    }

    public static VitalSignRecordPersistenceEntity toPersistenceFromDomain(VitalSignRecord vitalSignRecord) {
        var entity = new VitalSignRecordPersistenceEntity();

        entity.setPatientId(vitalSignRecord.getPatientId());
        entity.setNurseId(vitalSignRecord.getNurseId());
        entity.setHeartRate(vitalSignRecord.getHeartRate());
        entity.setRespiratoryRate(vitalSignRecord.getRespiratoryRate());
        entity.setBloodPressure(new BloodPressurePersistenceEmbeddable(
                vitalSignRecord.getBloodPressure().systolic(),
                vitalSignRecord.getBloodPressure().diastolic()
        ));
        entity.setOxygenSaturation(vitalSignRecord.getOxygenSaturation());
        entity.setTemperature(vitalSignRecord.getTemperature());
        entity.setRiskLevel(vitalSignRecord.getRiskLevel());
        entity.setRecordedAt(vitalSignRecord.getRecordedAt());

        return entity;
    }

    public static VitalSignRecord toDomainFromPersistence(VitalSignRecordPersistenceEntity entity) {
        var bloodPressure = new BloodPressure(
                entity.getBloodPressure().getSystolic(),
                entity.getBloodPressure().getDiastolic()
        );

        return VitalSignRecord.reconstitute(
                entity.getId(),
                entity.getPatientId(),
                entity.getNurseId(),
                entity.getHeartRate(),
                entity.getRespiratoryRate(),
                bloodPressure,
                entity.getOxygenSaturation(),
                entity.getTemperature(),
                entity.getRiskLevel(),
                entity.getRecordedAt()
        );
    }
}