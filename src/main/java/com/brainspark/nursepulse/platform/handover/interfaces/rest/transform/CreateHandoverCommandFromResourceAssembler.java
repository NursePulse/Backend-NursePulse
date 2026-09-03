package com.brainspark.nursepulse.platform.handover.interfaces.rest.transform;

import com.brainspark.nursepulse.platform.handover.domain.model.commands.CreateHandoverCommand;
import com.brainspark.nursepulse.platform.handover.interfaces.rest.resources.CreateHandoverResource;


public class CreateHandoverCommandFromResourceAssembler {

    public static CreateHandoverCommand toCommandFromResource(CreateHandoverResource resource) {
        return new CreateHandoverCommand(resource.patientId(), resource.title(), resource.description());
    }
}
