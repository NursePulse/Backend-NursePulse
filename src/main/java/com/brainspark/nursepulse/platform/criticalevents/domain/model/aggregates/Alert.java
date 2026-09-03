package com.brainspark.nursepulse.platform.criticalevents.domain.model.aggregates;

import com.brainspark.nursepulse.platform.criticalevents.domain.exceptions.InvalidAlertException;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.commands.AttendAlertCommand;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.commands.CloseAlertCommand;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.commands.CreateAlertCommand;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.valueobjects.AlertSeverity;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.valueobjects.AlertStatus;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.valueobjects.AlertType;
import com.brainspark.nursepulse.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
public class Alert extends AbstractDomainAggregateRoot<Alert> {

    @Setter
    private Long id;

    private Long patientId;
    private AlertType type;
    private AlertSeverity severity;
    private String description;
    private AlertStatus status;
    private String triggeredBy;

    private String attendedBy;
    private LocalDateTime attendedAt;

    private String closedBy;
    private String resolutionNotes;
    private LocalDateTime closedAt;

    public Alert() {
    }

    public Alert(CreateAlertCommand command) {
        validate(command);

        this.patientId = command.patientId();
        this.type = command.type();
        this.severity = command.severity();
        this.description = command.description().trim();
        this.triggeredBy = command.triggeredBy().trim();
        this.status = AlertStatus.OPEN;
    }

    private Alert(
            Long id,
            Long patientId,
            AlertType type,
            AlertSeverity severity,
            String description,
            AlertStatus status,
            String triggeredBy,
            String attendedBy,
            LocalDateTime attendedAt,
            String closedBy,
            String resolutionNotes,
            LocalDateTime closedAt
    ) {
        this.id = id;
        this.patientId = patientId;
        this.type = type;
        this.severity = severity;
        this.description = description;
        this.status = status;
        this.triggeredBy = triggeredBy;
        this.attendedBy = attendedBy;
        this.attendedAt = attendedAt;
        this.closedBy = closedBy;
        this.resolutionNotes = resolutionNotes;
        this.closedAt = closedAt;
    }

    /**
     * Marca la alerta como atendida.
     * Regla de negocio: solo una alerta OPEN puede ser atendida.
     */
    public void attend(AttendAlertCommand command) {
        if (command.attendedBy() == null || command.attendedBy().isBlank()) {
            throw new InvalidAlertException("Attended by is required");
        }

        if (this.status != AlertStatus.OPEN) {
            throw new InvalidAlertException(
                    "Only an OPEN alert can be attended (current status: %s)".formatted(this.status));
        }

        this.attendedBy = command.attendedBy().trim();
        this.attendedAt = LocalDateTime.now();
        this.status = AlertStatus.ATTENDED;
    }

    /**
     * Cierra la alerta.
     * Regla de negocio: se puede cerrar una alerta OPEN o ATTENDED,
     * pero nunca una que ya esté CLOSED.
     */
    public void close(CloseAlertCommand command) {
        if (command.closedBy() == null || command.closedBy().isBlank()) {
            throw new InvalidAlertException("Closed by is required");
        }

        if (command.resolutionNotes() == null || command.resolutionNotes().isBlank()) {
            throw new InvalidAlertException("Resolution notes are required");
        }

        if (this.status == AlertStatus.CLOSED) {
            throw new InvalidAlertException("Alert is already closed");
        }

        this.closedBy = command.closedBy().trim();
        this.resolutionNotes = command.resolutionNotes().trim();
        this.closedAt = LocalDateTime.now();
        this.status = AlertStatus.CLOSED;
    }

    private void validate(CreateAlertCommand command) {
        if (command.patientId() == null || command.patientId() <= 0) {
            throw new InvalidAlertException("Patient id is required");
        }

        if (command.type() == null) {
            throw new InvalidAlertException("Type is required");
        }

        if (command.severity() == null) {
            throw new InvalidAlertException("Severity is required");
        }

        if (command.description() == null || command.description().isBlank()) {
            throw new InvalidAlertException("Description is required");
        }

        if (command.triggeredBy() == null || command.triggeredBy().isBlank()) {
            throw new InvalidAlertException("Triggered by is required");
        }
    }

    public static Alert reconstitute(
            Long id,
            Long patientId,
            AlertType type,
            AlertSeverity severity,
            String description,
            AlertStatus status,
            String triggeredBy,
            String attendedBy,
            LocalDateTime attendedAt,
            String closedBy,
            String resolutionNotes,
            LocalDateTime closedAt
    ) {
        return new Alert(
                id,
                patientId,
                type,
                severity,
                description,
                status,
                triggeredBy,
                attendedBy,
                attendedAt,
                closedBy,
                resolutionNotes,
                closedAt
        );
    }
}
