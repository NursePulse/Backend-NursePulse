package com.brainspark.nursepulse.platform.handover.domain.model.aggregates;

import com.brainspark.nursepulse.platform.handover.domain.model.commands.CreateHandoverCommand;
import com.brainspark.nursepulse.platform.handover.domain.model.valueobjects.HandoverStatus;
import com.brainspark.nursepulse.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.util.Strings;

import java.util.Date;

@Getter
public class Handover extends AbstractDomainAggregateRoot<Handover> {

    @Setter
    private Long id;

    @Setter
    private Date createdAt;

    @Setter
    private Long patientId;

    @Setter
    private String title;

    @Setter
    private String description;

    @Setter
    private String situation;

    @Setter
    private String background;

    @Setter
    private String assessment;

    @Setter
    private String recommendation;

    @Setter
    private String registeredBy;

    @Setter
    private Long targetNurseId;

    @Setter
    private HandoverStatus status;

    @Setter
    private Long incomingNurseId;

    @Setter
    private String additionalNotes;

    public Handover() {
        this.title = Strings.EMPTY;
        this.description = Strings.EMPTY;
        this.situation = Strings.EMPTY;
        this.background = Strings.EMPTY;
        this.assessment = Strings.EMPTY;
        this.recommendation = Strings.EMPTY;
        this.registeredBy = Strings.EMPTY;
        this.status = HandoverStatus.PENDING;
    }


    public Handover(CreateHandoverCommand command) {
        this.patientId = command.patientId();
        this.title = command.title();
        this.situation = command.situation();
        this.background = command.background();
        this.assessment = command.assessment();
        this.recommendation = command.recommendation();
        this.registeredBy = command.registeredBy();
        this.targetNurseId = command.targetNurseId();
        this.description = buildDescription(command.situation(), command.background(), command.assessment(), command.recommendation());
        this.status = HandoverStatus.PENDING;
    }

    /**
     * Builds a plain-text summary of the structured SBAR sections.
     * <p>
     *     Kept for backward compatibility with clients that only read {@code description}
     *     (for example, records created before the SBAR sections were modeled as
     *     dedicated fields).
     * </p>
     */
    public static String buildDescription(String situation, String background, String assessment, String recommendation) {
        return "S: %s | B: %s | A: %s | R: %s".formatted(situation, background, assessment, recommendation);
    }

    public Handover updateInformation(String title, String description) {
        this.title = title;
        this.description = description;
        return this;
    }

    public void acknowledge(Long incomingNurseId, String additionalNotes) {
        this.incomingNurseId = incomingNurseId;
        this.additionalNotes = additionalNotes;
        this.status = HandoverStatus.ACKNOWLEDGED;
    }
}
