package com.brainspark.nursepulse.platform.reports.domain.repositories;

import com.brainspark.nursepulse.platform.reports.domain.model.aggregates.Report;

import java.util.List;

public interface ReportRepository {
    List<Report> findAll();

    Report save(Report report);
}
