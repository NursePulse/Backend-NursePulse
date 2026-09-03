package com.brainspark.nursepulse.platform.clinicalevents.interfaces.rest.transform;

import com.brainspark.nursepulse.platform.clinicalevents.domain.model.commands.CreateClinicalEventCommand;
import com.brainspark.nursepulse.platform.clinicalevents.domain.model.valueobjects.ClinicalEventSeverity;
import com.brainspark.nursepulse.platform.clinicalevents.domain.model.valueobjects.ClinicalEventType;
import com.brainspark.nursepulse.platform.clinicalevents.interfaces.rest.resources.CreateClinicalEventResource;

import java.util.Locale;

/**
 * Assembler that converts {@link CreateClinicalEventResource} objects into {@link CreateClinicalEventCommand} commands.
 */
public class CreateClinicalEventCommandFromResourceAssembler {
    /**
     * Converts a create clinical event resource to its command representation.
     *
     * @param resource create clinical event resource
     * @param registeredBy username of the clinical staff member registering the event
     * @return create clinical event command
     */
    public static CreateClinicalEventCommand toCommandFromResource(
            CreateClinicalEventResource resource,
            String registeredBy
    ) {
        return new CreateClinicalEventCommand(
                resource.patientId(),
                ClinicalEventType.valueOf(resource.eventType().trim().toUpperCase(Locale.ROOT)),
                ClinicalEventSeverity.valueOf(resource.severity().trim().toUpperCase(Locale.ROOT)),
                resource.title(),
                resource.description(),
                registeredBy
        );
    }
}
