package com.brainspark.nursepulse.platform.vitalsigns.interfaces.rest.transform;

import com.brainspark.nursepulse.platform.vitalsigns.domain.model.aggregates.VitalSignRecord;
import com.brainspark.nursepulse.platform.vitalsigns.interfaces.rest.resources.VitalSignRecordResource;

public final class VitalSignRecordResourceFromEntityAssembler {

    private VitalSignRecordResourceFromEntityAssembler() {
    }

    public static VitalSignRecordResource toResourceFromEntity(VitalSignRecord entity) {
        return new VitalSignRecordResource(
                entity.getId(),
                entity.getPatientId(),
                entity.getNurseId(),
                entity.getHeartRate(),
                entity.getRespiratoryRate(),
                entity.getBloodPressure().systolic(),
                entity.getBloodPressure().diastolic(),
                entity.getOxygenSaturation(),
                entity.getTemperature(),
                entity.getRiskLevel(),
                entity.getRecordedAt()
        );
    }
}