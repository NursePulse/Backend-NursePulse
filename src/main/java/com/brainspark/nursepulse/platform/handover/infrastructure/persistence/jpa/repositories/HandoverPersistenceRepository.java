package com.brainspark.nursepulse.platform.handover.infrastructure.persistence.jpa.repositories;

import com.brainspark.nursepulse.platform.handover.infrastructure.persistence.jpa.entities.HandoverPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface HandoverPersistenceRepository extends JpaRepository<HandoverPersistenceEntity, Long> {
    Optional<HandoverPersistenceEntity> findByTitle(String title);

    boolean existsByTitle(String title);

    List<HandoverPersistenceEntity> findByPatientId(Long patientId);
    List<HandoverPersistenceEntity> findByPatientIdAndCreatedAtBetween(Long patientId, Date startDate, Date endDate);
}
