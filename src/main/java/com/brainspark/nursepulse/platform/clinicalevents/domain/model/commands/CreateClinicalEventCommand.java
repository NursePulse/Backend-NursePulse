package com.brainspark.nursepulse.platform.clinicalevents.domain.model.commands;

import com.brainspark.nursepulse.platform.clinicalevents.domain.model.valueobjects.ClinicalEventSeverity;
import com.brainspark.nursepulse.platform.clinicalevents.domain.model.valueobjects.ClinicalEventType;

/**
 * Create clinical event command
 * <p>
 *     This class represents the command to register a clinical event for a patient.
 * </p>
 * @param patientId the identifier of the patient involved in the event
 * @param eventType the type of clinical event
 * @param severity the severity of the event
 * @param title the short title of the event
 * @param description the clinical description of the event
 * @param registeredBy the username of the clinical staff member who registered the event
 *
 * @see com.brainspark.nursepulse.platform.clinicalevents.domain.model.aggregates.ClinicalEvent
 */
public record CreateClinicalEventCommand(
        Long patientId,
        ClinicalEventType eventType,
        ClinicalEventSeverity severity,
        String title,
        String description,
        String registeredBy
) {
}
