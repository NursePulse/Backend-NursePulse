package com.brainspark.nursepulse.platform.patients.application.commandservices;

import com.brainspark.nursepulse.platform.patients.domain.model.aggregates.Patient;
import com.brainspark.nursepulse.platform.patients.domain.model.commands.CreatePatientCommand;
import com.brainspark.nursepulse.platform.patients.domain.model.commands.DeletePatientCommand;
import com.brainspark.nursepulse.platform.patients.domain.model.commands.UpdatePatientCommand;
import com.brainspark.nursepulse.platform.shared.application.result.ApplicationError;
import com.brainspark.nursepulse.platform.shared.application.result.Result;

public interface PatientCommandService {

    Result<Patient, ApplicationError> handle(CreatePatientCommand command);

    Result<Patient, ApplicationError> handle(UpdatePatientCommand command);

    Result<Void, ApplicationError> handle(DeletePatientCommand command);
}