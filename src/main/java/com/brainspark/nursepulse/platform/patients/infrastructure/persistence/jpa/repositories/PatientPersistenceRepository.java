package com.brainspark.nursepulse.platform.patients.infrastructure.persistence.jpa.repositories;

import com.brainspark.nursepulse.platform.patients.infrastructure.persistence.jpa.entities.PatientPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientPersistenceRepository extends JpaRepository<PatientPersistenceEntity, Long> {

    boolean existsByDocumentNumber(String documentNumber);
}
