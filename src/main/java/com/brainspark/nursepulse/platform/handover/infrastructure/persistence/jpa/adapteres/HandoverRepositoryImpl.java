package com.brainspark.nursepulse.platform.handover.infrastructure.persistence.jpa.adapters;

import com.brainspark.nursepulse.platform.handover.domain.model.aggregates.Handover;
import com.brainspark.nursepulse.platform.handover.domain.repositories.HandoverRepository;
import com.brainspark.nursepulse.platform.handover.infrastructure.persistence.jpa.assemblers.HandoverPersistenceAssembler;
import com.brainspark.nursepulse.platform.handover.infrastructure.persistence.jpa.repositories.HandoverPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class HandoverRepositoryImpl implements HandoverRepository {

    private final HandoverPersistenceRepository handoverPersistenceRepository;

    public HandoverRepositoryImpl(HandoverPersistenceRepository handoverPersistenceRepository) {
        this.handoverPersistenceRepository = handoverPersistenceRepository;
    }

    @Override
    public Optional<Handover> findById(Long id) {
        return handoverPersistenceRepository.findById(id).map(HandoverPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Handover> findAll() {
        return handoverPersistenceRepository.findAll().stream().map(HandoverPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public Optional<Handover> findByTitle(String title) {
        return handoverPersistenceRepository.findByTitle(title).map(HandoverPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Handover> findByPatientId(Long patientId) {
        return handoverPersistenceRepository.findByPatientId(patientId).stream().map(HandoverPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<Handover> findByPatientIdAndDateRange(Long patientId, Date startDate, Date endDate) {
        return handoverPersistenceRepository.findByPatientIdAndCreatedAtBetween(patientId, startDate, endDate).stream().map(HandoverPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public Handover save(Handover handover) {
        var saved = handoverPersistenceRepository.save(HandoverPersistenceAssembler.toPersistenceFromDomain(handover));
        return HandoverPersistenceAssembler.toDomainFromPersistence(saved);
    }

    @Override
    public boolean existsById(Long id) {
        return handoverPersistenceRepository.existsById(id);
    }

    @Override
    public boolean existsByTitle(String title) {
        return handoverPersistenceRepository.existsByTitle(title);
    }

    @Override
    public void deleteById(Long id) {
        handoverPersistenceRepository.deleteById(id);
    }
}
