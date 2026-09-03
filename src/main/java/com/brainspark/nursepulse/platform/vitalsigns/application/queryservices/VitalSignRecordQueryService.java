package com.brainspark.nursepulse.platform.vitalsigns.application.queryservices;

import com.brainspark.nursepulse.platform.shared.application.result.ApplicationError;
import com.brainspark.nursepulse.platform.shared.application.result.Result;
import com.brainspark.nursepulse.platform.vitalsigns.domain.model.aggregates.VitalSignRecord;
import com.brainspark.nursepulse.platform.vitalsigns.domain.model.queries.GetAllVitalSignRecordsQuery;
import com.brainspark.nursepulse.platform.vitalsigns.domain.model.queries.GetLatestVitalSignRecordByPatientIdQuery;
import com.brainspark.nursepulse.platform.vitalsigns.domain.model.queries.GetVitalSignRecordByIdQuery;
import com.brainspark.nursepulse.platform.vitalsigns.domain.model.queries.GetVitalSignRecordsByPatientIdQuery;

import java.util.List;

public interface VitalSignRecordQueryService {

    Result<VitalSignRecord, ApplicationError> handle(GetVitalSignRecordByIdQuery query);

    Result<List<VitalSignRecord>, ApplicationError> handle(GetAllVitalSignRecordsQuery query);

    Result<List<VitalSignRecord>, ApplicationError> handle(GetVitalSignRecordsByPatientIdQuery query);

    Result<VitalSignRecord, ApplicationError> handle(GetLatestVitalSignRecordByPatientIdQuery query);
}