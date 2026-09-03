package com.brainspark.nursepulse.platform.auditlogs.interfaces.REST.transform;

import com.brainspark.nursepulse.platform.auditlogs.domain.model.commands.CreateAuditLogCommand;
import com.brainspark.nursepulse.platform.auditlogs.interfaces.REST.resources.CreateAuditLogResource;
//import tools.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
//import org.jspecify.annotations.Nullable;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Stateless assembler that maps an inbound {@link CreateAuditLogResource} to a domain command.
 */
public final class CreateAuditLogCommandFromResourceAssembler {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private CreateAuditLogCommandFromResourceAssembler() {}

    public static CreateAuditLogCommand toCommandFromResource(
            CreateAuditLogResource resource,
            String authenticatedActor
    ) {
        if (authenticatedActor == null || authenticatedActor.isBlank()) {
            throw new IllegalArgumentException("Authenticated actor is required");
        }

        String metadataJson = serializeMetadata(resource.metadata());
        return new CreateAuditLogCommand(
                resource.patientId(),
                resource.entityType(),
                resource.entityId(),
                resource.actionType(),
                authenticatedActor,
                resource.performedAt(),
                metadataJson
                //serializeMetadata(resource.metadata())
        );
    }



    /**
     * Converts metadata Object (which may be a Map, String, or null) to a JSON string,
     * or returns null if metadata is null.
     */
    //@Nullable
    private static String serializeMetadata(Object metadata) {
        if (metadata == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(metadata);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid metadata JSON structure", e);
        }
    }

}
