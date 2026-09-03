package com.brainspark.nursepulse.platform.vitalsigns.domain.repositories;

import com.brainspark.nursepulse.platform.vitalsigns.domain.model.aggregates.VitalSignRecord;

import java.util.List;
import java.util.Optional;

public interface VitalSignRecordRepository {
    Optional<VitalSignRecord> findById(Long id);

    List<VitalSignRecord> findAll();

    List<VitalSignRecord> findByPatientId(Long patientId);

    Optional<VitalSignRecord> findLatestByPatientId(Long patientId);

    VitalSignRecord save(VitalSignRecord vitalSignRecord);

    boolean existsById(Long id);

    void deleteById(Long id);
}
