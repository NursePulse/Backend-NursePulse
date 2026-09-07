package com.brainspark.nursepulse.platform.reports.infrastructure.persistence.jpa.adapters;

import com.brainspark.nursepulse.platform.reports.domain.model.aggregates.Report;
import com.brainspark.nursepulse.platform.reports.domain.repositories.ReportRepository;
import com.brainspark.nursepulse.platform.reports.infrastructure.persistence.jpa.assemblers.ReportPersistenceAssembler;
import com.brainspark.nursepulse.platform.reports.infrastructure.persistence.jpa.repositories.ReportPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReportRepositoryImpl implements ReportRepository {

    private final ReportPersistenceRepository reportPersistenceRepository;

    public ReportRepositoryImpl(ReportPersistenceRepository reportPersistenceRepository) {
        this.reportPersistenceRepository = reportPersistenceRepository;
    }

    @Override
    public List<Report> findAll() {
        return reportPersistenceRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(ReportPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public Report save(Report report) {
        var saved = reportPersistenceRepository.save(ReportPersistenceAssembler.toPersistenceFromDomain(report));
        return ReportPersistenceAssembler.toDomainFromPersistence(saved);
    }
}
