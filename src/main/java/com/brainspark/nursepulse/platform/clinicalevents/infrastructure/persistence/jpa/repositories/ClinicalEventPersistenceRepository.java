package com.brainspark.nursepulse.platform.clinicalevents.infrastructure.persistence.jpa.repositories;

import com.brainspark.nursepulse.platform.clinicalevents.infrastructure.persistence.jpa.entities.ClinicalEventPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for clinical event persistence entities.
 */
@Repository
public interface ClinicalEventPersistenceRepository extends JpaRepository<ClinicalEventPersistenceEntity, Long> {
    List<ClinicalEventPersistenceEntity> findByPatientId(Long patientId);
}
