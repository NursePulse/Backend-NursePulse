package com.brainspark.nursepulse.platform.auditlogs.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

/**
 * Embeddable value object that stores free-form contextual details about the audited action.
 * Stored as a raw JSON string in the database column {@code metadata}.
 * The value is optional, but it's helpful to the objective of this bounded context
 */
@Embeddable
@Getter
public class AuditMetadata {

    @Nullable
    @Column(name = "metadata", columnDefinition = "TEXT")
    private final String value;

    protected AuditMetadata() {
        this.value = null;
    }

    private AuditMetadata(@Nullable String value) {
        this.value = value;
    }

    public static AuditMetadata of(@Nullable String value) {
        return new AuditMetadata(value);
    }

    public static AuditMetadata empty() {
        return new AuditMetadata(null);
    }
}