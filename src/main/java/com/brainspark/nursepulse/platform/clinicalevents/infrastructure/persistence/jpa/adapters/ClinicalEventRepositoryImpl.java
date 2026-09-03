package com.brainspark.nursepulse.platform.clinicalevents.infrastructure.persistence.jpa.adapters;

import com.brainspark.nursepulse.platform.clinicalevents.domain.model.aggregates.ClinicalEvent;
import com.brainspark.nursepulse.platform.clinicalevents.domain.repositories.ClinicalEventRepository;
import com.brainspark.nursepulse.platform.clinicalevents.infrastructure.persistence.jpa.assemblers.ClinicalEventPersistenceAssembler;
import com.brainspark.nursepulse.platform.clinicalevents.infrastructure.persistence.jpa.repositories.ClinicalEventPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository adapter that bridges the clinical event domain repository port with Spring Data JPA.
 */
@Repository
public class ClinicalEventRepositoryImpl implements ClinicalEventRepository {

    private final ClinicalEventPersistenceRepository clinicalEventPersistenceRepository;

    public ClinicalEventRepositoryImpl(ClinicalEventPersistenceRepository clinicalEventPersistenceRepository) {
        this.clinicalEventPersistenceRepository = clinicalEventPersistenceRepository;
    }

    @Override
    public List<ClinicalEvent> findAll() {
        return clinicalEventPersistenceRepository.findAll().stream()
                .map(ClinicalEventPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<ClinicalEvent> findByPatientId(Long patientId) {
        return clinicalEventPersistenceRepository.findByPatientId(patientId).stream()
                .map(ClinicalEventPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public ClinicalEvent save(ClinicalEvent clinicalEvent) {
        var saved = clinicalEventPersistenceRepository.save(
                ClinicalEventPersistenceAssembler.toPersistenceFromDomain(clinicalEvent));
        return ClinicalEventPersistenceAssembler.toDomainFromPersistence(saved);
    }
}
