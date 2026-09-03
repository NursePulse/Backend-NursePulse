package com.brainspark.nursepulse.platform.auditlogs.infrastructure.persistence.jpa.repositories;

import com.brainspark.nursepulse.platform.auditlogs.domain.model.aggregates.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Spring Data JPA repository for the {@link AuditLog} aggregate.
 * Append-only: no delete or update operations are exposed.
 * {@link JpaSpecificationExecutor} enables composable filtering for the paginates list endpoint without needing a method per each filter combination
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long>, JpaSpecificationExecutor<AuditLog> {
}