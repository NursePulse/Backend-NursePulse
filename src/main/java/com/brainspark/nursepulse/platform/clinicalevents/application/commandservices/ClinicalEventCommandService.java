package com.brainspark.nursepulse.platform.clinicalevents.application.commandservices;

import com.brainspark.nursepulse.platform.clinicalevents.domain.model.aggregates.ClinicalEvent;
import com.brainspark.nursepulse.platform.clinicalevents.domain.model.commands.CreateClinicalEventCommand;
import com.brainspark.nursepulse.platform.shared.application.result.ApplicationError;
import com.brainspark.nursepulse.platform.shared.application.result.Result;

/**
 * Application service contract for clinical event commands.
 */
public interface ClinicalEventCommandService {
    /**
     * Handles the registration of a clinical event.
     *
     * @param command create clinical event command
     * @return created clinical event aggregate, or an application error
     */
    Result<ClinicalEvent, ApplicationError> handle(CreateClinicalEventCommand command);
}
