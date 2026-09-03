package com.brainspark.nursepulse.platform.criticalevents.application.queryservices;

import com.brainspark.nursepulse.platform.criticalevents.domain.model.aggregates.Alert;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.queries.GetAlertByIdQuery;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.queries.GetAlertsByPatientIdQuery;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.queries.GetAllAlertsQuery;
import com.brainspark.nursepulse.platform.shared.application.result.ApplicationError;
import com.brainspark.nursepulse.platform.shared.application.result.Result;

import java.util.List;

public interface AlertQueryService {

    Result<Alert, ApplicationError> handle(GetAlertByIdQuery query);

    Result<List<Alert>, ApplicationError> handle(GetAllAlertsQuery query);

    Result<List<Alert>, ApplicationError> handle(GetAlertsByPatientIdQuery query);
}
