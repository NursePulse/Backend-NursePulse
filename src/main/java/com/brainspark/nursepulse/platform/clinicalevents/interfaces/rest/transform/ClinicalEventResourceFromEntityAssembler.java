package com.brainspark.nursepulse.platform.clinicalevents.interfaces.rest.transform;

import com.brainspark.nursepulse.platform.clinicalevents.domain.model.aggregates.ClinicalEvent;
import com.brainspark.nursepulse.platform.clinicalevents.interfaces.rest.resources.ClinicalEventResource;

/**
 * Assembler that converts {@link ClinicalEvent} aggregates into REST {@link ClinicalEventResource} objects.
 */
public class ClinicalEventResourceFromEntityAssembler {
    /**
     * Converts a clinical event aggregate to its REST representation.
     *
     * @param clinicalEvent clinical event aggregate
     * @return clinical event resource
     */
    public static ClinicalEventResource toResourceFromEntity(ClinicalEvent clinicalEvent) {
        return new ClinicalEventResource(
                clinicalEvent.getId(),
                clinicalEvent.getPatientId(),
                clinicalEvent.getEventType().name(),
                clinicalEvent.getSeverity().name(),
                clinicalEvent.getTitle(),
                clinicalEvent.getDescription(),
                clinicalEvent.getRegisteredBy(),
                clinicalEvent.getOccurredAt().toString()
        );
    }
}
