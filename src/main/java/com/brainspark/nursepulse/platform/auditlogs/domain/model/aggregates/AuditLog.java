package com.brainspark.nursepulse.platform.auditlogs.domain.model.aggregates;

import com.brainspark.nursepulse.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.brainspark.nursepulse.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import com.brainspark.nursepulse.platform.auditlogs.domain.model.commands.CreateAuditLogCommand;
import com.brainspark.nursepulse.platform.auditlogs.domain.model.valueobjects.AuditActionType;
import com.brainspark.nursepulse.platform.auditlogs.domain.model.valueobjects.AuditMetadata;
import com.brainspark.nursepulse.platform.auditlogs.domain.model.valueobjects.AuditedEntityType;
import jakarta.persistence.*;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

/**
 * Aggregate root for the auditlogs bounded context.
 *
 * Represents an immutable record of a clinical action. After construction via the command factory method, no state may be changed. No setters are exposed beyond those inherited from
 * the shared persistence superclass.
 *
 * The table name resolves to {@code audit_logs} via the snake_case pluralized naming strategy.
 */
@Entity
@Getter
public class AuditLog extends AuditableAbstractPersistenceEntity {

    /** Nullable — absent for global/non-patient-scoped actions. */
    @Nullable
    @Column(name = "patient_id")
    private Long patientId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private AuditedEntityType entityType;

    @Column(nullable = false, updatable = false)
    private String entityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private AuditActionType actionType;

    @Column(nullable = false, updatable = false)
    private String performedBy;

    @Column(nullable = false, updatable = false)
    private Instant performedAt;

    @Embedded
    private AuditMetadata metadata;

    protected AuditLog() {}

    /**
     * Factory method — the only valid way to create an {@link AuditLog}.
     * Defaults {@code performedAt} to the current instant when not supplied in the command.
     */
    public static AuditLog create(CreateAuditLogCommand command) {
        var entry = new AuditLog();
        entry.patientId   = command.patientId();
        entry.entityType  = command.entityType();
        entry.entityId    = command.entityId();
        entry.actionType  = command.actionType();
        entry.performedBy = command.performedBy();
        entry.performedAt = command.performedAt() != null ? command.performedAt() : Instant.now();
        entry.metadata    = AuditMetadata.of(command.metadata());
        return entry;
    }
}