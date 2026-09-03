package com.brainspark.nursepulse.platform.patients.infrastructure.persistence.jpa.adapters;

import com.brainspark.nursepulse.platform.patients.domain.model.aggregates.Patient;
import com.brainspark.nursepulse.platform.patients.domain.repositories.PatientRepository;
import com.brainspark.nursepulse.platform.patients.infrastructure.persistence.jpa.assemblers.PatientPersistenceAssembler;
import com.brainspark.nursepulse.platform.patients.infrastructure.persistence.jpa.repositories.PatientPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PatientRepositoryImpl implements PatientRepository {

    private final PatientPersistenceRepository patientPersistenceRepository;

    public PatientRepositoryImpl(PatientPersistenceRepository patientPersistenceRepository) {
        this.patientPersistenceRepository = patientPersistenceRepository;
    }

    @Override
    public Patient save(Patient patient) {
        var entity = PatientPersistenceAssembler.toPersistenceFromDomain(patient);
        var savedEntity = patientPersistenceRepository.save(entity);

        return PatientPersistenceAssembler.toDomainFromPersistence(savedEntity);
    }

    @Override
    public boolean existsByDocumentNumber(String documentNumber) {
        return patientPersistenceRepository.existsByDocumentNumber(documentNumber);
    }

    @Override
    public Optional<Patient> findById(Long id) {
        return patientPersistenceRepository.findById(id)
                .map(PatientPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Patient> findAll() {
        return patientPersistenceRepository.findAll()
                .stream()
                .map(PatientPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        patientPersistenceRepository.deleteById(id);
    }
}