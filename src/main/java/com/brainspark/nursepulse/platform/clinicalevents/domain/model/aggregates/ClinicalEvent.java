package com.brainspark.nursepulse.platform.clinicalevents.domain.model.aggregates;

import com.brainspark.nursepulse.platform.clinicalevents.domain.model.commands.CreateClinicalEventCommand;
import com.brainspark.nursepulse.platform.clinicalevents.domain.model.valueobjects.ClinicalEventSeverity;
import com.brainspark.nursepulse.platform.clinicalevents.domain.model.valueobjects.ClinicalEventType;
import com.brainspark.nursepulse.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Clinical event aggregate root.
 * <p>
 *     Represents an operational clinical event registered by the care team
 *     during a shift, such as an administered medication, a procedure, or a
 *     change in the patient's condition.
 * </p>
 */
@Getter
public class ClinicalEvent extends AbstractDomainAggregateRoot<ClinicalEvent> {

    @Setter
    private Long id;

    @Setter
    private Long patientId;

    @Setter
    private ClinicalEventType eventType;

    @Setter
    private ClinicalEventSeverity severity;

    @Setter
    private String title;

    @Setter
    private String description;

    @Setter
    private String registeredBy;

    @Setter
    private LocalDateTime occurredAt;

    public ClinicalEvent() {
    }

    public ClinicalEvent(CreateClinicalEventCommand command) {
        this.patientId = command.patientId();
        this.eventType = command.eventType();
        this.severity = command.severity();
        this.title = command.title();
        this.description = command.description();
        this.registeredBy = command.registeredBy();
        this.occurredAt = LocalDateTime.now();
    }
}
