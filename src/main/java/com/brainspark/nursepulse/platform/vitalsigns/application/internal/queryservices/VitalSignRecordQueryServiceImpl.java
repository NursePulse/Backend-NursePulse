package com.brainspark.nursepulse.platform.vitalsigns.application.internal.queryservices;

import com.brainspark.nursepulse.platform.shared.application.result.ApplicationError;
import com.brainspark.nursepulse.platform.shared.application.result.Result;
import com.brainspark.nursepulse.platform.vitalsigns.application.queryservices.VitalSignRecordQueryService;
import com.brainspark.nursepulse.platform.vitalsigns.domain.model.aggregates.VitalSignRecord;
import com.brainspark.nursepulse.platform.vitalsigns.domain.model.queries.GetAllVitalSignRecordsQuery;
import com.brainspark.nursepulse.platform.vitalsigns.domain.model.queries.GetLatestVitalSignRecordByPatientIdQuery;
import com.brainspark.nursepulse.platform.vitalsigns.domain.model.queries.GetVitalSignRecordByIdQuery;
import com.brainspark.nursepulse.platform.vitalsigns.domain.model.queries.GetVitalSignRecordsByPatientIdQuery;
import com.brainspark.nursepulse.platform.vitalsigns.domain.repositories.VitalSignRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VitalSignRecordQueryServiceImpl implements VitalSignRecordQueryService {

    private final VitalSignRecordRepository vitalSignRecordRepository;

    public VitalSignRecordQueryServiceImpl(VitalSignRecordRepository vitalSignRecordRepository) {
        this.vitalSignRecordRepository = vitalSignRecordRepository;
    }

    @Override
    public Result<VitalSignRecord, ApplicationError> handle(GetVitalSignRecordByIdQuery query) {
        return vitalSignRecordRepository.findById(query.vitalSignRecordId())
                .<Result<VitalSignRecord, ApplicationError>>map(Result::success)
                .orElseGet(() -> Result.failure(ApplicationError.notFound(
                        "Vital sign record",
                        query.vitalSignRecordId().toString()
                )));
    }

    @Override
    public Result<List<VitalSignRecord>, ApplicationError> handle(GetAllVitalSignRecordsQuery query) {
        var vitalSignRecords = vitalSignRecordRepository.findAll();

        return Result.success(vitalSignRecords);
    }

    @Override
    public Result<List<VitalSignRecord>, ApplicationError> handle(GetVitalSignRecordsByPatientIdQuery query) {
        var vitalSignRecords = vitalSignRecordRepository.findByPatientId(query.patientId());

        return Result.success(vitalSignRecords);
    }

    @Override
    public Result<VitalSignRecord, ApplicationError> handle(GetLatestVitalSignRecordByPatientIdQuery query) {
        return vitalSignRecordRepository.findLatestByPatientId(query.patientId())
                .<Result<VitalSignRecord, ApplicationError>>map(Result::success)
                .orElseGet(() -> Result.failure(ApplicationError.notFound(
                        "Latest vital sign record for patient",
                        query.patientId().toString()
                )));
    }
}