package com.brainspark.nursepulse.platform.patients.application.internal.commandservices;

import com.brainspark.nursepulse.platform.patients.application.commandservices.PatientCommandService;
import com.brainspark.nursepulse.platform.patients.domain.exceptions.PatientAlreadyExistsException;
import com.brainspark.nursepulse.platform.patients.domain.model.aggregates.Patient;
import com.brainspark.nursepulse.platform.patients.domain.model.commands.CreatePatientCommand;
import com.brainspark.nursepulse.platform.patients.domain.model.commands.DeletePatientCommand;
import com.brainspark.nursepulse.platform.patients.domain.model.commands.UpdatePatientCommand;
import com.brainspark.nursepulse.platform.patients.domain.repositories.PatientRepository;
import com.brainspark.nursepulse.platform.shared.application.result.ApplicationError;
import com.brainspark.nursepulse.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

@Service
public class PatientCommandServiceImpl implements PatientCommandService {

    private final PatientRepository patientRepository;

    public PatientCommandServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public Result<Patient, ApplicationError> handle(CreatePatientCommand command) {
        try {
            if (patientRepository.existsByDocumentNumber(command.documentNumber())) {
                throw new PatientAlreadyExistsException(command.documentNumber());
            }

            var patient = new Patient(command);
            var savedPatient = patientRepository.save(patient);

            return Result.success(savedPatient);
        } catch (PatientAlreadyExistsException exception) {
            return Result.failure(ApplicationError.conflict(
                    "Patient",
                    exception.getMessage()
            ));
        } catch (RuntimeException exception) {
            return Result.failure(ApplicationError.businessRuleViolation(
                    "create patient",
                    exception.getMessage()
            ));
        }
    }

    @Override
    public Result<Patient, ApplicationError> handle(UpdatePatientCommand command) {
        try {
            var patientOptional = patientRepository.findById(command.patientId());

            if (patientOptional.isEmpty()) {
                return Result.failure(ApplicationError.notFound(
                        "Patient",
                        "Patient with id %s was not found.".formatted(command.patientId())
                ));
            }

            var patient = patientOptional.get();

            if (!patient.getDocumentNumber().equals(command.documentNumber())
                    && patientRepository.existsByDocumentNumber(command.documentNumber())) {
                throw new PatientAlreadyExistsException(command.documentNumber());
            }

            patient.update(command);
            var updatedPatient = patientRepository.save(patient);

            return Result.success(updatedPatient);
        } catch (PatientAlreadyExistsException exception) {
            return Result.failure(ApplicationError.conflict(
                    "Patient",
                    exception.getMessage()
            ));
        } catch (RuntimeException exception) {
            return Result.failure(ApplicationError.businessRuleViolation(
                    "update patient",
                    exception.getMessage()
            ));
        }
    }

    @Override
    public Result<Void, ApplicationError> handle(DeletePatientCommand command) {
        try {
            if (command.patientId() == null || command.patientId() <= 0) {
                return Result.failure(ApplicationError.businessRuleViolation(
                        "delete patient",
                        "Patient id is required"
                ));
            }

            var patientOptional = patientRepository.findById(command.patientId());

            if (patientOptional.isEmpty()) {
                return Result.failure(ApplicationError.notFound(
                        "Patient",
                        "Patient with id %s was not found.".formatted(command.patientId())
                ));
            }

            patientRepository.deleteById(command.patientId());

            return Result.success(null);
        } catch (RuntimeException exception) {
            return Result.failure(ApplicationError.businessRuleViolation(
                    "delete patient",
                    exception.getMessage()
            ));
        }
    }
}