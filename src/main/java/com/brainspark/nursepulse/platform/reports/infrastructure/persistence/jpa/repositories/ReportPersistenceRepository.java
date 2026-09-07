package com.brainspark.nursepulse.platform.reports.infrastructure.persistence.jpa.repositories;

import com.brainspark.nursepulse.platform.reports.infrastructure.persistence.jpa.entities.ReportPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportPersistenceRepository extends JpaRepository<ReportPersistenceEntity, Long> {
    List<ReportPersistenceEntity> findAllByOrderByCreatedAtDesc();
}
