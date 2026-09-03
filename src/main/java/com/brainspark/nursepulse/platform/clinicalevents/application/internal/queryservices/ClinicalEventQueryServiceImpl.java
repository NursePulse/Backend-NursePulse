package com.brainspark.nursepulse.platform.clinicalevents.application.internal.queryservices;

import com.brainspark.nursepulse.platform.clinicalevents.application.queryservices.ClinicalEventQueryService;
import com.brainspark.nursepulse.platform.clinicalevents.domain.model.aggregates.ClinicalEvent;
import com.brainspark.nursepulse.platform.clinicalevents.domain.model.queries.GetAllClinicalEventsQuery;
import com.brainspark.nursepulse.platform.clinicalevents.domain.model.queries.GetClinicalEventsByPatientIdQuery;
import com.brainspark.nursepulse.platform.clinicalevents.domain.repositories.ClinicalEventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Clinical event query service implementation.
 */
@Service
public class ClinicalEventQueryServiceImpl implements ClinicalEventQueryService {

    private final ClinicalEventRepository clinicalEventRepository;

    public ClinicalEventQueryServiceImpl(ClinicalEventRepository clinicalEventRepository) {
        this.clinicalEventRepository = clinicalEventRepository;
    }

    @Override
    public List<ClinicalEvent> handle(GetAllClinicalEventsQuery query) {
        return clinicalEventRepository.findAll();
    }

    @Override
    public List<ClinicalEvent> handle(GetClinicalEventsByPatientIdQuery query) {
        return clinicalEventRepository.findByPatientId(query.patientId());
    }
}
