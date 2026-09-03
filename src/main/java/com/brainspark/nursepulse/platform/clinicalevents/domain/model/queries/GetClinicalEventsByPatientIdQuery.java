package com.brainspark.nursepulse.platform.clinicalevents.domain.model.queries;

/**
 * Query to retrieve the clinical events of a patient.
 *
 * @param patientId the patient identifier
 */
public record GetClinicalEventsByPatientIdQuery(Long patientId) {
}
