package com.brainspark.nursepulse.platform.clinicalevents.application.queryservices;

import com.brainspark.nursepulse.platform.clinicalevents.domain.model.aggregates.ClinicalEvent;
import com.brainspark.nursepulse.platform.clinicalevents.domain.model.queries.GetAllClinicalEventsQuery;
import com.brainspark.nursepulse.platform.clinicalevents.domain.model.queries.GetClinicalEventsByPatientIdQuery;

import java.util.List;

/**
 * Application service contract for clinical event queries.
 */
public interface ClinicalEventQueryService {
    /**
     * Handles the retrieval of all clinical events.
     *
     * @param query get all clinical events query
     * @return list of clinical events
     */
    List<ClinicalEvent> handle(GetAllClinicalEventsQuery query);

    /**
     * Handles the retrieval of the clinical events of a patient.
     *
     * @param query get clinical events by patient id query
     * @return list of clinical events for the patient
     */
    List<ClinicalEvent> handle(GetClinicalEventsByPatientIdQuery query);
}
