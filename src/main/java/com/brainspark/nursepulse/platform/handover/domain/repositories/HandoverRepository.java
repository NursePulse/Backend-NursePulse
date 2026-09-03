package com.brainspark.nursepulse.platform.handover.domain.repositories;

import com.brainspark.nursepulse.platform.handover.domain.model.aggregates.Handover;

import java.util.Date;
import java.util.List;
import java.util.Optional;


public interface HandoverRepository {
    Optional<Handover> findById(Long id);

    List<Handover> findAll();

    Optional<Handover> findByTitle(String title);
    
    List<Handover> findByPatientId(Long patientId);
    
    List<Handover> findByPatientIdAndDateRange(Long patientId, Date startDate, Date endDate);

    Handover save(Handover handover);

    boolean existsById(Long id);

    boolean existsByTitle(String title);

    void deleteById(Long id);
}
