package com.brainspark.nursepulse.platform.vitalsigns.domain.model.queries;

import com.brainspark.nursepulse.platform.vitalsigns.domain.exceptions.InvalidVitalSignRecordException;

import java.time.LocalDateTime;

/**
 * Query to retrieve the vital sign records of a patient.
 * <p>
 *     {@code from} and {@code to} are optional; when both are provided, results
 *     are restricted to the requested clinical period.
 * </p>
 */
public record GetVitalSignRecordsByPatientIdQuery(Long patientId, LocalDateTime from, LocalDateTime to) {
    public GetVitalSignRecordsByPatientIdQuery {
        if (patientId == null || patientId <= 0) {
            throw new InvalidVitalSignRecordException("Patient id must be greater than zero");
        }
    }

    public GetVitalSignRecordsByPatientIdQuery(Long patientId) {
        this(patientId, null, null);
    }
}
