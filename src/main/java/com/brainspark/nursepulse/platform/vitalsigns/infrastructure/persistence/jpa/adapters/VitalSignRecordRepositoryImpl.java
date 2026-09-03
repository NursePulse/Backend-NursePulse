package com.brainspark.nursepulse.platform.vitalsigns.infrastructure.persistence.jpa.adapters;

import com.brainspark.nursepulse.platform.vitalsigns.domain.model.aggregates.VitalSignRecord;
import com.brainspark.nursepulse.platform.vitalsigns.domain.repositories.VitalSignRecordRepository;
import com.brainspark.nursepulse.platform.vitalsigns.infrastructure.persistence.jpa.assemblers.VitalSignRecordPersistenceAssembler;
import com.brainspark.nursepulse.platform.vitalsigns.infrastructure.persistence.jpa.repositories.VitalSignRecordPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class VitalSignRecordRepositoryImpl implements VitalSignRecordRepository {

    private final VitalSignRecordPersistenceRepository vitalSignRecordPersistenceRepository;

    public VitalSignRecordRepositoryImpl(VitalSignRecordPersistenceRepository vitalSignRecordPersistenceRepository) {
        this.vitalSignRecordPersistenceRepository = vitalSignRecordPersistenceRepository;
    }

    @Override
    public Optional<VitalSignRecord> findById(Long id) {
        return vitalSignRecordPersistenceRepository.findById(id)
                .map(VitalSignRecordPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<VitalSignRecord> findAll() {
        return vitalSignRecordPersistenceRepository.findAll()
                .stream()
                .map(VitalSignRecordPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<VitalSignRecord> findByPatientId(Long patientId) {
        return vitalSignRecordPersistenceRepository.findByPatientId(patientId)
                .stream()
                .map(VitalSignRecordPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public VitalSignRecord save(VitalSignRecord vitalSignRecord) {
        var entity = VitalSignRecordPersistenceAssembler.toPersistenceFromDomain(vitalSignRecord);
        var savedEntity = vitalSignRecordPersistenceRepository.save(entity);

        return VitalSignRecordPersistenceAssembler.toDomainFromPersistence(savedEntity);
    }
    @Override
    public Optional<VitalSignRecord> findLatestByPatientId(Long patientId) {
        return vitalSignRecordPersistenceRepository.findFirstByPatientIdOrderByRecordedAtDesc(patientId)
                .map(VitalSignRecordPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public boolean existsById(Long id) {
        return vitalSignRecordPersistenceRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        vitalSignRecordPersistenceRepository.deleteById(id);
    }
}