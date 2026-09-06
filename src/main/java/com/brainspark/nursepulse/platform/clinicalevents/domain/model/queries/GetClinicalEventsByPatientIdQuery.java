package com.brainspark.nursepulse.platform.clinicalevents.domain.model.queries;

import java.time.LocalDateTime;

/**
 * Query to retrieve the clinical events of a patient.
 * <p>
 *     {@code from} and {@code to} are optional; when both are provided, results
 *     are restricted to the requested clinical period.
 * </p>
 *
 * @param patientId the patient identifier
 * @param from optional start of the clinical period
 * @param to optional end of the clinical period
 */
public record GetClinicalEventsByPatientIdQuery(Long patientId, LocalDateTime from, LocalDateTime to) {
    public GetClinicalEventsByPatientIdQuery(Long patientId) {
        this(patientId, null, null);
    }
}
