package com.brainspark.nursepulse.platform.vitalsigns.interfaces.rest.transform;

import com.brainspark.nursepulse.platform.vitalsigns.domain.model.commands.CreateVitalSignRecordCommand;
import com.brainspark.nursepulse.platform.vitalsigns.domain.model.valueobjects.BloodPressure;
import com.brainspark.nursepulse.platform.vitalsigns.interfaces.rest.resources.CreateVitalSignRecordResource;

public final class CreateVitalSignRecordCommandFromResourceAssembler {

    private CreateVitalSignRecordCommandFromResourceAssembler() {
    }

    public static CreateVitalSignRecordCommand toCommandFromResource(CreateVitalSignRecordResource resource) {
        return new CreateVitalSignRecordCommand(
                resource.patientId(),
                resource.nurseId(),
                resource.heartRate(),
                resource.respiratoryRate(),
                new BloodPressure(resource.systolicPressure(), resource.diastolicPressure()),
                resource.oxygenSaturation(),
                resource.temperature(),
                resource.recordedAt()
        );
    }
}