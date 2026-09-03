package com.brainspark.nursepulse.platform.criticalevents.application.commandservices;

import com.brainspark.nursepulse.platform.criticalevents.domain.model.aggregates.Alert;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.commands.AttendAlertCommand;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.commands.CloseAlertCommand;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.commands.CreateAlertCommand;
import com.brainspark.nursepulse.platform.shared.application.result.ApplicationError;
import com.brainspark.nursepulse.platform.shared.application.result.Result;

public interface AlertCommandService {

    Result<Alert, ApplicationError> handle(CreateAlertCommand command);

    Result<Alert, ApplicationError> handle(AttendAlertCommand command);

    Result<Alert, ApplicationError> handle(CloseAlertCommand command);
}
