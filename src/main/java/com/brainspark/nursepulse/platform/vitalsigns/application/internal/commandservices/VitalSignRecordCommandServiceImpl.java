package com.brainspark.nursepulse.platform.vitalsigns.application.internal.commandservices;

import com.brainspark.nursepulse.platform.shared.application.result.ApplicationError;
import com.brainspark.nursepulse.platform.shared.application.result.Result;
import com.brainspark.nursepulse.platform.vitalsigns.application.commandservices.VitalSignRecordCommandService;
import com.brainspark.nursepulse.platform.vitalsigns.domain.model.aggregates.VitalSignRecord;
import com.brainspark.nursepulse.platform.vitalsigns.domain.model.commands.CreateVitalSignRecordCommand;
import com.brainspark.nursepulse.platform.vitalsigns.domain.repositories.VitalSignRecordRepository;
import org.springframework.stereotype.Service;

@Service
public class VitalSignRecordCommandServiceImpl implements VitalSignRecordCommandService {

    private final VitalSignRecordRepository vitalSignRecordRepository;

    public VitalSignRecordCommandServiceImpl(VitalSignRecordRepository vitalSignRecordRepository) {
        this.vitalSignRecordRepository = vitalSignRecordRepository;
    }

    @Override
    public Result<VitalSignRecord, ApplicationError> handle(CreateVitalSignRecordCommand command) {
        try {
            var vitalSignRecord = new VitalSignRecord(command);
            var savedVitalSignRecord = vitalSignRecordRepository.save(vitalSignRecord);

            return Result.success(savedVitalSignRecord);
        } catch (RuntimeException exception) {
            return Result.failure(ApplicationError.businessRuleViolation(
                    "create vital sign record",
                    exception.getMessage()
            ));
        }
    }
}