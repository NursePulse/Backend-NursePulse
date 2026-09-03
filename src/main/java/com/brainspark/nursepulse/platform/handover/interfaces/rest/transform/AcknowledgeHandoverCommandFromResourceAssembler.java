package com.brainspark.nursepulse.platform.handover.interfaces.rest.transform;

import com.brainspark.nursepulse.platform.handover.domain.model.commands.AcknowledgeHandoverCommand;
import com.brainspark.nursepulse.platform.handover.interfaces.rest.resources.AcknowledgeHandoverResource;

public class AcknowledgeHandoverCommandFromResourceAssembler {

    public static AcknowledgeHandoverCommand toCommandFromResource(Long handoverId, AcknowledgeHandoverResource resource) {
        return new AcknowledgeHandoverCommand(handoverId, resource.incomingNurseId(), resource.additionalNotes());
    }
}
