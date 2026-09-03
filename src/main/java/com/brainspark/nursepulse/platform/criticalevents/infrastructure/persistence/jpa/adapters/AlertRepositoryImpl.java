package com.brainspark.nursepulse.platform.criticalevents.infrastructure.persistence.jpa.adapters;

import com.brainspark.nursepulse.platform.criticalevents.domain.model.aggregates.Alert;
import com.brainspark.nursepulse.platform.criticalevents.domain.repositories.AlertRepository;
import com.brainspark.nursepulse.platform.criticalevents.infrastructure.persistence.jpa.assemblers.AlertPersistenceAssembler;
import com.brainspark.nursepulse.platform.criticalevents.infrastructure.persistence.jpa.repositories.AlertPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AlertRepositoryImpl implements AlertRepository {

    private final AlertPersistenceRepository alertPersistenceRepository;

    public AlertRepositoryImpl(AlertPersistenceRepository alertPersistenceRepository) {
        this.alertPersistenceRepository = alertPersistenceRepository;
    }

    @Override
    public Alert save(Alert alert) {
        var entity = AlertPersistenceAssembler.toPersistenceFromDomain(alert);
        var savedEntity = alertPersistenceRepository.save(entity);

        return AlertPersistenceAssembler.toDomainFromPersistence(savedEntity);
    }

    @Override
    public Optional<Alert> findById(Long id) {
        return alertPersistenceRepository.findById(id)
                .map(AlertPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Alert> findAll() {
        return alertPersistenceRepository.findAll()
                .stream()
                .map(AlertPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<Alert> findByPatientId(Long patientId) {
        return alertPersistenceRepository.findByPatientId(patientId)
                .stream()
                .map(AlertPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }
}
