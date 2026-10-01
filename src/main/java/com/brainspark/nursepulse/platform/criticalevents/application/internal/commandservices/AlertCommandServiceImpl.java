package com.brainspark.nursepulse.platform.criticalevents.application.internal.commandservices;

import com.brainspark.nursepulse.platform.criticalevents.application.commandservices.AlertCommandService;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.aggregates.Alert;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.commands.AttendAlertCommand;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.commands.CloseAlertCommand;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.commands.CreateAlertCommand;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.valueobjects.AlertSeverity;
import com.brainspark.nursepulse.platform.criticalevents.domain.repositories.AlertRepository;
import com.brainspark.nursepulse.platform.iam.domain.repositories.UserRepository;
import com.brainspark.nursepulse.platform.shared.application.notifications.SmsNotificationService;
import com.brainspark.nursepulse.platform.shared.application.result.ApplicationError;
import com.brainspark.nursepulse.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

@Service
public class AlertCommandServiceImpl implements AlertCommandService {

    private final AlertRepository alertRepository;
    private final SmsNotificationService smsNotificationService;
    private final UserRepository userRepository;

    public AlertCommandServiceImpl(
            AlertRepository alertRepository,
            SmsNotificationService smsNotificationService,
            UserRepository userRepository
    ) {
        this.alertRepository = alertRepository;
        this.smsNotificationService = smsNotificationService;
        this.userRepository = userRepository;
    }

    @Override
    public Result<Alert, ApplicationError> handle(CreateAlertCommand command) {
        try {
            var alert = new Alert(command);
            var savedAlert = alertRepository.save(alert);

            if (savedAlert.getSeverity() == AlertSeverity.CRITICAL) {
                userRepository.findAll().stream()
                        .filter(u -> u.getRoles() != null && u.getRoles().stream().anyMatch(r -> "ROLE_DOCTOR".equals(r.getStringName())))
                        .filter(u -> u.getPhone() != null && !u.getPhone().isBlank())
                        .forEach(doctor -> smsNotificationService.sendCriticalAlertSms(
                                doctor.getPhone(),
                                "Alerta critica en paciente #" + savedAlert.getPatientId() + ": " + savedAlert.getDescription()
                        ));
            }

            return Result.success(savedAlert);
        } catch (RuntimeException exception) {
            return Result.failure(ApplicationError.businessRuleViolation(
                    "create alert",
                    exception.getMessage()
            ));
        }
    }

    @Override
    public Result<Alert, ApplicationError> handle(AttendAlertCommand command) {
        try {
            var alertOptional = alertRepository.findById(command.alertId());

            if (alertOptional.isEmpty()) {
                return Result.failure(ApplicationError.notFound(
                        "Alert",
                        "Alert with id %s was not found.".formatted(command.alertId())
                ));
            }

            var alert = alertOptional.get();
            alert.attend(command);
            var updatedAlert = alertRepository.save(alert);

            return Result.success(updatedAlert);
        } catch (RuntimeException exception) {
            return Result.failure(ApplicationError.businessRuleViolation(
                    "attend alert",
                    exception.getMessage()
            ));
        }
    }

    @Override
    public Result<Alert, ApplicationError> handle(CloseAlertCommand command) {
        try {
            var alertOptional = alertRepository.findById(command.alertId());

            if (alertOptional.isEmpty()) {
                return Result.failure(ApplicationError.notFound(
                        "Alert",
                        "Alert with id %s was not found.".formatted(command.alertId())
                ));
            }

            var alert = alertOptional.get();
            alert.close(command);
            var updatedAlert = alertRepository.save(alert);

            return Result.success(updatedAlert);
        } catch (RuntimeException exception) {
            return Result.failure(ApplicationError.businessRuleViolation(
                    "close alert",
                    exception.getMessage()
            ));
        }
    }
}
