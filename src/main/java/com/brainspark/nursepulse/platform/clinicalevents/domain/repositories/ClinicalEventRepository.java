package com.brainspark.nursepulse.platform.clinicalevents.domain.repositories;

import com.brainspark.nursepulse.platform.clinicalevents.domain.model.aggregates.ClinicalEvent;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Clinical event repository port.
 */
public interface ClinicalEventRepository {
    List<ClinicalEvent> findAll();

    List<ClinicalEvent> findByPatientId(Long patientId);

    List<ClinicalEvent> findByPatientIdAndDateRange(Long patientId, LocalDateTime from, LocalDateTime to);

    ClinicalEvent save(ClinicalEvent clinicalEvent);
}
