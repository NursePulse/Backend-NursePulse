package com.brainspark.nursepulse.platform.patients.interfaces.rest.transform;

import com.brainspark.nursepulse.platform.patients.domain.model.commands.CreatePatientCommand;
import com.brainspark.nursepulse.platform.patients.interfaces.rest.resources.CreatePatientResource;

public final class CreatePatientCommandFromResourceAssembler {

    private CreatePatientCommandFromResourceAssembler() {
    }

    public static CreatePatientCommand toCommandFromResource(CreatePatientResource resource) {
        return new CreatePatientCommand(
                resource.firstName(),
                resource.lastName(),
                resource.documentNumber(),
                resource.birthDate(),
                resource.gender(),
                resource.diagnosis(),
                resource.roomNumber(),
                resource.bedNumber(),
                resource.attendingPhysician(),
                resource.status(),
                resource.admissionDate()
        );
    }
}
