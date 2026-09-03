package com.brainspark.nursepulse.platform.patients.domain.repositories;

import com.brainspark.nursepulse.platform.patients.domain.model.aggregates.Patient;

import java.util.List;
import java.util.Optional;

public interface PatientRepository {

    Patient save(Patient patient);

    boolean existsByDocumentNumber(String documentNumber);

    Optional<Patient> findById(Long id);

    List<Patient> findAll();

    void deleteById(Long id);
}