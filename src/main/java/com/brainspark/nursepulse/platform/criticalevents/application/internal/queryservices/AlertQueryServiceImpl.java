package com.brainspark.nursepulse.platform.criticalevents.application.internal.queryservices;

import com.brainspark.nursepulse.platform.criticalevents.application.queryservices.AlertQueryService;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.aggregates.Alert;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.queries.GetAlertByIdQuery;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.queries.GetAlertsByPatientIdQuery;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.queries.GetAllAlertsQuery;
import com.brainspark.nursepulse.platform.criticalevents.domain.repositories.AlertRepository;
import com.brainspark.nursepulse.platform.shared.application.result.ApplicationError;
import com.brainspark.nursepulse.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertQueryServiceImpl implements AlertQueryService {

    private final AlertRepository alertRepository;

    public AlertQueryServiceImpl(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @Override
    public Result<Alert, ApplicationError> handle(GetAlertByIdQuery query) {
        return alertRepository.findById(query.alertId())
                .map(Result::<Alert, ApplicationError>success)
                .orElseGet(() -> Result.failure(ApplicationError.notFound(
                        "Alert",
                        "Alert with id %s was not found.".formatted(query.alertId())
                )));
    }

    @Override
    public Result<List<Alert>, ApplicationError> handle(GetAllAlertsQuery query) {
        var alerts = alertRepository.findAll();

        return Result.success(alerts);
    }

    @Override
    public Result<List<Alert>, ApplicationError> handle(GetAlertsByPatientIdQuery query) {
        var alerts = alertRepository.findByPatientId(query.patientId());

        return Result.success(alerts);
    }
}
