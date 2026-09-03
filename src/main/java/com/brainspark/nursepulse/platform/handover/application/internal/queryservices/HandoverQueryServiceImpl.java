package com.brainspark.nursepulse.platform.handover.application.internal.queryservices;

import com.brainspark.nursepulse.platform.handover.application.queryservices.HandoverQueryService;
import com.brainspark.nursepulse.platform.handover.domain.model.aggregates.Handover;
import com.brainspark.nursepulse.platform.handover.domain.model.queries.GetAllHandoversByPatientIdQuery;
import com.brainspark.nursepulse.platform.handover.domain.model.queries.GetAllHandoversQuery;
import com.brainspark.nursepulse.platform.handover.domain.model.queries.GetHandoverByIdQuery;
import com.brainspark.nursepulse.platform.handover.domain.repositories.HandoverRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HandoverQueryServiceImpl implements HandoverQueryService {

    private final HandoverRepository handoverRepository;

    public HandoverQueryServiceImpl(HandoverRepository handoverRepository) {
        this.handoverRepository = handoverRepository;
    }

    @Override
    public Optional<Handover> handle(GetHandoverByIdQuery query) {
        return handoverRepository.findById(query.handoverId());
    }

    @Override
    public List<Handover> handle(GetAllHandoversQuery query) {
        return handoverRepository.findAll();
    }

    @Override
    public List<Handover> handle(GetAllHandoversByPatientIdQuery query) {
        if (query.startDate() != null && query.endDate() != null) {
            return handoverRepository.findByPatientIdAndDateRange(query.patientId(), query.startDate(), query.endDate());
        }
        return handoverRepository.findByPatientId(query.patientId());
    }
}
