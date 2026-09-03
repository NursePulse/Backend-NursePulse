package com.brainspark.nursepulse.platform.auditlogs.domain.model.queries;

import org.jspecify.annotations.Nullable;
import com.brainspark.nursepulse.platform.auditlogs.domain.model.valueobjects.AuditedEntityType;
import com.brainspark.nursepulse.platform.auditlogs.domain.model.valueobjects.AuditActionType;

import java.time.Instant;

/**
 * Bounded Context query that carries the intent to retrieve a paginated, filtered list of audit log entries.
 * All filter fields are optional/nullable. Any combination may be supplied.
 * This query is intentionally generic so it can be reused by future features of auditlogs.
 * @param patientId nullable -> restrict results to a specific patient
 * @param entityType nullable -> restrict results to a specific audited entity type
 * @param entityId nullable -> restrict results to a specific audited entity instance
 * @param actionType nullable -> restrict results to a specific action type
 * @param performedBy nullable -> restrict results to actions performed by a specific actor
 * @param from nullable -> returns entries whose {@code performedAt} is greater than or equal to this value
 * @param to nullable -> returns entries whose {@code performedAt} is less than or equal to this value
 * @param page zero based page index. Defaults to 0 when null
 * @param size page size. Defaults to 20 when null
 */
public record GetAuditLogsQuery(
        @Nullable Long patientId, @Nullable AuditedEntityType entityType, @Nullable String entityId,
        @Nullable AuditActionType actionType, @Nullable String performedBy, @Nullable Instant from,
        @Nullable Instant to, @Nullable Integer page, @Nullable Integer size
) {
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 200;

    /**
     * @return the zero based page index, defaulting to {@code 0} and never negative.
     */
    public int pageOrDefault(){
        return (page == null || page < 0) ? DEFAULT_PAGE : page;
    }

    /**
     * @return the page size, defaulting to {@code 20}, never less than 1 and capped at {@code 200} to keep queries efficient and clean.
     */
    public int sizeOrDefault(){
        if (size == null || size <= 0)
            return DEFAULT_SIZE;
        return Math.min(size, MAX_SIZE);
    }
}
