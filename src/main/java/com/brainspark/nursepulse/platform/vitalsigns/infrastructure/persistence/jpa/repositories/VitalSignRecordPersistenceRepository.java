package com.brainspark.nursepulse.platform.vitalsigns.infrastructure.persistence.jpa.repositories;

import com.brainspark.nursepulse.platform.vitalsigns.infrastructure.persistence.jpa.entities.VitalSignRecordPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VitalSignRecordPersistenceRepository
        extends JpaRepository<VitalSignRecordPersistenceEntity, Long> {

    List<VitalSignRecordPersistenceEntity> findByPatientId(Long patientId);

    List<VitalSignRecordPersistenceEntity> findByPatientIdAndRecordedAtBetween(
            Long patientId, LocalDateTime from, LocalDateTime to);

    Optional<VitalSignRecordPersistenceEntity> findFirstByPatientIdOrderByRecordedAtDesc(Long patientId);
}