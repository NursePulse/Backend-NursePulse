package com.brainspark.nursepulse.platform.criticalevents.domain.repositories;

import com.brainspark.nursepulse.platform.criticalevents.domain.model.aggregates.Alert;

import java.util.List;
import java.util.Optional;

public interface AlertRepository {

    Alert save(Alert alert);

    Optional<Alert> findById(Long id);

    List<Alert> findAll();

    List<Alert> findByPatientId(Long patientId);
}
