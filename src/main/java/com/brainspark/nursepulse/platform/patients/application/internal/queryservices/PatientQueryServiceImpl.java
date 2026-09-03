package com.brainspark.nursepulse.platform.patients.application.internal.queryservices;

import com.brainspark.nursepulse.platform.patients.application.queryservices.PatientQueryService;
import com.brainspark.nursepulse.platform.patients.domain.model.aggregates.Patient;
import com.brainspark.nursepulse.platform.patients.domain.model.queries.GetAllPatientsQuery;
import com.brainspark.nursepulse.platform.patients.domain.model.queries.GetPatientByIdQuery;
import com.brainspark.nursepulse.platform.patients.domain.repositories.PatientRepository;
import com.brainspark.nursepulse.platform.shared.application.result.ApplicationError;
import com.brainspark.nursepulse.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientQueryServiceImpl implements PatientQueryService {

    private final PatientRepository patientRepository;

    public PatientQueryServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public Result<Patient, ApplicationError> handle(GetPatientByIdQuery query) {
        return patientRepository.findById(query.patientId())
                .map(Result::<Patient, ApplicationError>success)
                .orElseGet(() -> Result.failure(ApplicationError.notFound(
                        "Patient",
                        "Patient with id %s was not found.".formatted(query.patientId())
                )));
    }

    @Override
    public Result<List<Patient>, ApplicationError> handle(GetAllPatientsQuery query) {
        var patients = patientRepository.findAll();

        return Result.success(patients);
    }
}