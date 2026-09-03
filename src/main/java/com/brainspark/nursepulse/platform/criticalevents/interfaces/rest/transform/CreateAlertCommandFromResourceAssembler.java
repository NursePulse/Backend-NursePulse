package com.brainspark.nursepulse.platform.criticalevents.interfaces.rest.transform;

import com.brainspark.nursepulse.platform.criticalevents.domain.model.commands.CreateAlertCommand;
import com.brainspark.nursepulse.platform.criticalevents.interfaces.rest.resources.CreateAlertResource;

public final class CreateAlertCommandFromResourceAssembler {

    private CreateAlertCommandFromResourceAssembler() {
    }

    public static CreateAlertCommand toCommandFromResource(CreateAlertResource resource) {
        return new CreateAlertCommand(
                resource.patientId(),
                resource.type(),
                resource.severity(),
                resource.description(),
                resource.triggeredBy()
        );
    }
}
