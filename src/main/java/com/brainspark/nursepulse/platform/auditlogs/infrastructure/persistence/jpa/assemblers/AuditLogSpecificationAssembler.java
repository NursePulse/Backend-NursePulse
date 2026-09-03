package com.brainspark.nursepulse.platform.auditlogs.infrastructure.persistence.jpa.assemblers;

import com.brainspark.nursepulse.platform.auditlogs.domain.model.aggregates.AuditLog;
import com.brainspark.nursepulse.platform.auditlogs.domain.model.queries.GetAuditLogsQuery;
import com.brainspark.nursepulse.platform.auditlogs.domain.model.queries.GetPatientAuditTimelineQuery;
import com.brainspark.nursepulse.platform.auditlogs.domain.model.queries.GetEntityAuditHistoryQuery;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * This class builds composable {@link Specification} instances for {@link AuditLog} from the various query types used across the auditlogs bounded context.
 * Each filter is applied only when present, so any combination of filters (included none) is fully supported.
 * Each factory method applied only the filters that are present in its query, keeping the query service free of conditional branching.
 */
public final class AuditLogSpecificationAssembler {
    private AuditLogSpecificationAssembler(){}

    /**
     * Builds a specification from a generic filtered list query
     * Every field is optional and any combination, including none, is supported.
     */
    public static Specification<AuditLog> fromQuery(GetAuditLogsQuery query) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (query.patientId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("patientId"), query.patientId()));
            }
            if (query.entityType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("entityType"), query.entityType()));
            }
            if (query.entityId() != null && !query.entityId().isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("entityId"), query.entityId()));
            }
            if (query.actionType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("actionType"), query.actionType()));
            }
            if (query.performedBy() != null && !query.performedBy().isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("performedBy"), query.performedBy()));
            }
            if (query.from() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("performedAt"), query.from()));
            }
            if (query.to() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("performedAt"), query.to()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Builds a specification for the patient audit timeline query
     * Always filters on {@code patientId}.
     */
    public static Specification<AuditLog> fromPatientTimelineQuery(GetPatientAuditTimelineQuery query) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("patientId"), query.patientId()));

            if (query.from() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("performedAt"), query.from()));
            }
            if (query.to() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("performedAt"), query.to()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Builds a specification for the entity audit history query.
     * It always filters on both {@code entityType} and {@code entityId}, which together uniquely identify a concrete clinical entity instance.
     */
    public static Specification<AuditLog> fromEntityHistoryQuery(GetEntityAuditHistoryQuery query){
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.equal(root.get("entityType"), query.entityType()),
                criteriaBuilder.equal(root.get("entityId"), query.entityId())
        );
    }
}