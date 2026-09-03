package com.brainspark.nursepulse.platform.vitalsigns.application.commandservices;

import com.brainspark.nursepulse.platform.shared.application.result.ApplicationError;
import com.brainspark.nursepulse.platform.shared.application.result.Result;
import com.brainspark.nursepulse.platform.vitalsigns.domain.model.aggregates.VitalSignRecord;
import com.brainspark.nursepulse.platform.vitalsigns.domain.model.commands.CreateVitalSignRecordCommand;

public interface VitalSignRecordCommandService {

    Result<VitalSignRecord, ApplicationError> handle(CreateVitalSignRecordCommand command);
}