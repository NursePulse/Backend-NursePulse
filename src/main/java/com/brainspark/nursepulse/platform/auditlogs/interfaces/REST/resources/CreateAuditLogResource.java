package com.brainspark.nursepulse.platform.auditlogs.interfaces.REST.resources;

import com.brainspark.nursepulse.platform.auditlogs.domain.model.valueobjects.AuditActionType;
import com.brainspark.nursepulse.platform.auditlogs.domain.model.valueobjects.AuditedEntityType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.Nullable;
//import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import java.util.Map;

/**
 * Inbound REST resource for {@code POST /api/v1/audit-logs}.
 *
 * @param patientId   optional — omit for global actions not tied to a specific patient
 * @param entityType  the category of the audited clinical entity
 * @param entityId    the opaque string identifier of that entity
 * @param actionType  the nature (type) of the action
 * @param performedBy identifier of the actor (user id, service name, etc.)
 * @param performedAt optional ISO-8601 instant; the service defaults to {@code now()} when absent
 * @param metadata    optional JSON object with extra context (will be serialized to a string)
 *
 * @Schema and its parameters are used for swagger documentation, providing descriptions and examples for each field in the API docs.
 */
@Schema(description = "Payload for creating a new audit log entry")
public record CreateAuditLogResource(

        @Nullable
        @Schema(description = "Patient identifier — omit for global actions", example = "42")
        Long patientId,

        @NotNull(message = "entityType is required")
        @Schema(description = "Type of the audited entity", example = "VITAL_SIGNS")
        AuditedEntityType entityType,

        @NotBlank(message = "entityId must not be blank")
        @Schema(description = "Identifier of the audited entity", example = "7d3a1c")
        String entityId,

        @NotNull(message = "actionType is required")
        @Schema(description = "Type of the clinical action", example = "CREATE")
        AuditActionType actionType,

        @NotBlank(message = "performedBy must not be blank")
        @Schema(description = "Staff member who performed the action", example = "nurse-user-15")
        String performedBy,

        @Nullable
        @Schema(description = "Instant the action occurred (using ISO-8601). Defaults to server time when absent.",
                example = "2026-03-01T14:30:00Z")
        Instant performedAt,
        /*
        @Nullable
        @Schema(description = "Optional JSON string with additional contextual details",
                example = "{\"systolic\":120,\"diastolic\":80}")
        String metadata
        */
        @Nullable
        @Schema(description= "Optional metadata object with additional contextual details",
                example = """
                        {
                            "systolic": 120,
                            "diastolic": 80,
                            "notes": "Patient was calm and cooperative during measurement."
                        }
                        """
        )
        Map<String, Object> metadata
) {}