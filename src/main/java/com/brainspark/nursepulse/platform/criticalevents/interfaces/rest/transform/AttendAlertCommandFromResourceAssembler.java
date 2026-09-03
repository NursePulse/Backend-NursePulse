package com.brainspark.nursepulse.platform.criticalevents.interfaces.rest.transform;

import com.brainspark.nursepulse.platform.criticalevents.domain.model.commands.AttendAlertCommand;
import com.brainspark.nursepulse.platform.criticalevents.interfaces.rest.resources.AttendAlertResource;

public final class AttendAlertCommandFromResourceAssembler {

    private AttendAlertCommandFromResourceAssembler() {
    }

    public static AttendAlertCommand toCommandFromResource(Long alertId, AttendAlertResource resource) {
        return new AttendAlertCommand(
                alertId,
                resource.attendedBy()
        );
    }
}
