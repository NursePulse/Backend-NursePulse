package com.brainspark.nursepulse.platform.auditlogs.interfaces.REST.transform;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;

import java.util.Map;

/**
 * Shared utility for parsing the audit log {@code metadata} JSON string into a {@code Map<String, Object>}.
 * This avoids duplication in Assemblers, instead this centralizes it so all outbound assemblers use the same parsing behavior and warning message.
 */
@Slf4j
public class AuditLogMetadataDeserializer {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private AuditLogMetadataDeserializer(){}

    /**
     * This parses a stored metadata JSON string into a {@code Map<String, Object>}.
     * @param metadataJson the raw JSON string stored in the database or {@code null}
     * @return the parsed map, {@code null} when the input is null or blank, or an empty map whe the stored value is not a valid JSON object
     */
    @Nullable
    static Map<String, Object> deserialize(@Nullable String metadataJson){
        if(metadataJson == null || metadataJson.isBlank())
            return null;
        try{
            return OBJECT_MAPPER.readValue(metadataJson, new TypeReference<Map<String, Object>>() {});
        } catch(JsonProcessingException e){
            log.warn("Stored audit log metadata is not a valid JSON object. Returning empty map");
            return Map.of();
        }
    }
}
