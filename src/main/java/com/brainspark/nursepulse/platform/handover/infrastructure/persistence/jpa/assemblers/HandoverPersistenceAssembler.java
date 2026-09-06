package com.brainspark.nursepulse.platform.handover.infrastructure.persistence.jpa.assemblers;

import com.brainspark.nursepulse.platform.handover.domain.model.aggregates.Handover;
import com.brainspark.nursepulse.platform.handover.infrastructure.persistence.jpa.entities.HandoverPersistenceEntity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class HandoverPersistenceAssembler {

    private static final Pattern LEGACY_SECTION_PATTERN = Pattern.compile(
            "(Situation|Background|Assessment|Recommendation):\\s*(.*?)(?=\\n[A-Za-z]+:|$)",
            Pattern.DOTALL
    );

    private HandoverPersistenceAssembler() {
    }

    public static Handover toDomainFromPersistence(HandoverPersistenceEntity entity) {
        if (entity == null) return null;

        var handover = new Handover();
        handover.setId(entity.getId());
        handover.setCreatedAt(entity.getCreatedAt());
        handover.setPatientId(entity.getPatientId());
        handover.setTitle(entity.getTitle());
        handover.setDescription(entity.getDescription());
        handover.setRegisteredBy(entity.getRegisteredBy());
        handover.setStatus(entity.getStatus());
        handover.setIncomingNurseId(entity.getIncomingNurseId());
        handover.setAdditionalNotes(entity.getAdditionalNotes());

        if (hasStructuredSbar(entity)) {
            handover.setSituation(entity.getSituation());
            handover.setBackground(entity.getBackground());
            handover.setAssessment(entity.getAssessment());
            handover.setRecommendation(entity.getRecommendation());
        } else {
            // Backward compatibility: handovers created before the SBAR sections
            // were modeled as dedicated columns only have them encoded inside the
            // free-text description. Parse that legacy format on read so existing
            // records keep displaying correctly in the structured SBAR view.
            var legacy = parseLegacyDescription(entity.getDescription());
            handover.setSituation(legacy.getOrDefault("Situation", ""));
            handover.setBackground(legacy.getOrDefault("Background", ""));
            handover.setAssessment(legacy.getOrDefault("Assessment", ""));
            handover.setRecommendation(legacy.getOrDefault("Recommendation", ""));
        }

        return handover;
    }

    public static HandoverPersistenceEntity toPersistenceFromDomain(Handover handover) {
        if (handover == null) return null;

        var entity = new HandoverPersistenceEntity();
        // Only set ID if the handover is being updated (has a non-null ID)
        // For new handovers, leave ID null to allow JPA to generate it
        if (handover.getId() != null) {
            entity.setId(handover.getId());
        }
        entity.setPatientId(handover.getPatientId());
        entity.setTitle(handover.getTitle());
        entity.setDescription(handover.getDescription());
        entity.setSituation(handover.getSituation());
        entity.setBackground(handover.getBackground());
        entity.setAssessment(handover.getAssessment());
        entity.setRecommendation(handover.getRecommendation());
        entity.setRegisteredBy(handover.getRegisteredBy());
        entity.setStatus(handover.getStatus());
        entity.setIncomingNurseId(handover.getIncomingNurseId());
        entity.setAdditionalNotes(handover.getAdditionalNotes());
        return entity;
    }

    private static boolean hasStructuredSbar(HandoverPersistenceEntity entity) {
        return entity.getSituation() != null && !entity.getSituation().isBlank();
    }

    private static java.util.Map<String, String> parseLegacyDescription(String description) {
        var sections = new java.util.HashMap<String, String>();
        if (description == null) return sections;

        Matcher matcher = LEGACY_SECTION_PATTERN.matcher(description);
        while (matcher.find()) {
            sections.put(matcher.group(1), matcher.group(2).trim());
        }
        return sections;
    }
}
