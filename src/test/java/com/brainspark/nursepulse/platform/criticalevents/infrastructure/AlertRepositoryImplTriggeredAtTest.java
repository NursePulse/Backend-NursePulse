package com.brainspark.nursepulse.platform.criticalevents.infrastructure;

import com.brainspark.nursepulse.platform.criticalevents.domain.model.aggregates.Alert;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.commands.AttendAlertCommand;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.commands.CreateAlertCommand;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.valueobjects.AlertSeverity;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.valueobjects.AlertStatus;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.valueobjects.AlertType;
import com.brainspark.nursepulse.platform.criticalevents.domain.repositories.AlertRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Not transactional on purpose: each save commits, as it does in production, so the second save works
 * on a detached copy of the row. Covers US-32 (the attended alert keeps its generation date).
 */
@SpringBootTest
@ActiveProfiles("test")
class AlertRepositoryImplTriggeredAtTest {

    @Autowired
    private AlertRepository alertRepository;

    @Test
    void shouldKeepTriggeredAtWhenAnExistingAlertIsSaved() {
        var created = alertRepository.save(new Alert(new CreateAlertCommand(
                1L, AlertType.CARDIAC, AlertSeverity.HIGH, "Taquicardia", "nurse.itest")));
        assertNotNull(created.getTriggeredAt());

        var alert = alertRepository.findById(created.getId()).orElseThrow();
        alert.attend(new AttendAlertCommand(alert.getId(), "nurse.itest"));
        var attended = alertRepository.save(alert);

        assertEquals(AlertStatus.ATTENDED, attended.getStatus());
        assertNotNull(attended.getTriggeredAt());
        assertEquals(created.getTriggeredAt(), attended.getTriggeredAt());
    }
}
