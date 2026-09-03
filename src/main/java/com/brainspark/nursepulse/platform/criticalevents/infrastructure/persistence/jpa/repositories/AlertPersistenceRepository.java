package com.brainspark.nursepulse.platform.criticalevents.infrastructure.persistence.jpa.repositories;

import com.brainspark.nursepulse.platform.criticalevents.infrastructure.persistence.jpa.entities.AlertPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertPersistenceRepository extends JpaRepository<AlertPersistenceEntity, Long> {

    List<AlertPersistenceEntity> findByPatientId(Long patientId);
}
