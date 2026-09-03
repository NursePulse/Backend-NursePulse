package com.brainspark.nursepulse.platform.handover.application.commandservices;

import com.brainspark.nursepulse.platform.handover.domain.model.aggregates.Handover;
import com.brainspark.nursepulse.platform.handover.domain.model.commands.AcknowledgeHandoverCommand;
import com.brainspark.nursepulse.platform.handover.domain.model.commands.CreateHandoverCommand;
import com.brainspark.nursepulse.platform.shared.application.result.ApplicationError;
import com.brainspark.nursepulse.platform.shared.application.result.Result;

public interface HandoverCommandService {

    Result<Long, ApplicationError> handle(CreateHandoverCommand command);
    Result<Handover, ApplicationError> handle(AcknowledgeHandoverCommand command);
}
