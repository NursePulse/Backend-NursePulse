package com.brainspark.nursepulse.platform.clinicalevents.domain.repositories;

import com.brainspark.nursepulse.platform.clinicalevents.domain.model.aggregates.ClinicalEvent;

import java.util.List;

/**
 * Clinical event repository port.
 */
public interface ClinicalEventRepository {
    List<ClinicalEvent> findAll();

    List<ClinicalEvent> findByPatientId(Long patientId);

    ClinicalEvent save(ClinicalEvent clinicalEvent);
}
