package com.brainspark.nursepulse.platform.criticalevents.interfaces.rest.transform;

import com.brainspark.nursepulse.platform.criticalevents.domain.model.commands.CloseAlertCommand;
import com.brainspark.nursepulse.platform.criticalevents.interfaces.rest.resources.CloseAlertResource;

public final class CloseAlertCommandFromResourceAssembler {

    private CloseAlertCommandFromResourceAssembler() {
    }

    public static CloseAlertCommand toCommandFromResource(Long alertId, CloseAlertResource resource) {
        return new CloseAlertCommand(
                alertId,
                resource.closedBy(),
                resource.resolutionNotes()
        );
    }
}
