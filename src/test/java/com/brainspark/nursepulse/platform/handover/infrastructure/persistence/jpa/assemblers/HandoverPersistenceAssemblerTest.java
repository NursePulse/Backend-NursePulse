package com.brainspark.nursepulse.platform.handover.infrastructure.persistence.jpa.assemblers;

import com.brainspark.nursepulse.platform.handover.domain.model.valueobjects.HandoverStatus;
import com.brainspark.nursepulse.platform.handover.infrastructure.persistence.jpa.entities.HandoverPersistenceEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Covers the SBAR backward-compatibility path: handovers created before the
 * S/B/A/R sections were modeled as dedicated columns only had them encoded
 * inside the free-text {@code description}. Reading such a legacy row must
 * still populate the structured fields so existing data keeps displaying
 * correctly (see EP-02/EP-03 review notes on the SBAR restructuring).
 */
class HandoverPersistenceAssemblerTest {

    @Test
    void parsesLegacyDescriptionWhenStructuredColumnsAreEmpty() {
        var entity = new HandoverPersistenceEntity();
        entity.setPatientId(1L);
        entity.setTitle("SBAR - Legacy Patient");
        entity.setDescription(
                "ReceiverId: 2\nReceiverName: Enfermero Luis\n" +
                        "Situation: Patient stable, mild chest discomfort\n" +
                        "Background: Admitted for unstable angina, day 2\n" +
                        "Assessment: Vitals within range, pain controlled\n" +
                        "Recommendation: Continue monitoring, next troponin at 06:00"
        );
        entity.setStatus(HandoverStatus.PENDING);
        // situation/background/assessment/recommendation columns left null, as a legacy row would have them

        var handover = HandoverPersistenceAssembler.toDomainFromPersistence(entity);

        assertEquals("Patient stable, mild chest discomfort", handover.getSituation());
        assertEquals("Admitted for unstable angina, day 2", handover.getBackground());
        assertEquals("Vitals within range, pain controlled", handover.getAssessment());
        assertEquals("Continue monitoring, next troponin at 06:00", handover.getRecommendation());
    }

    @Test
    void usesStructuredColumnsDirectlyWhenPresent() {
        var entity = new HandoverPersistenceEntity();
        entity.setPatientId(1L);
        entity.setTitle("SBAR - New Patient");
        entity.setDescription("S: Stable | B: ... | A: ... | R: ...");
        entity.setSituation("Stable");
        entity.setBackground("Admitted yesterday");
        entity.setAssessment("Improving");
        entity.setRecommendation("Continue plan");
        entity.setStatus(HandoverStatus.PENDING);

        var handover = HandoverPersistenceAssembler.toDomainFromPersistence(entity);

        assertEquals("Stable", handover.getSituation());
        assertEquals("Admitted yesterday", handover.getBackground());
        assertEquals("Improving", handover.getAssessment());
        assertEquals("Continue plan", handover.getRecommendation());
    }
}
