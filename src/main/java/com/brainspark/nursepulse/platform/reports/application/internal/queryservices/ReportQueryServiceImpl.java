package com.brainspark.nursepulse.platform.reports.application.internal.queryservices;

import com.brainspark.nursepulse.platform.reports.application.queryservices.ReportQueryService;
import com.brainspark.nursepulse.platform.reports.domain.model.aggregates.Report;
import com.brainspark.nursepulse.platform.reports.domain.model.queries.GetAllReportsQuery;
import com.brainspark.nursepulse.platform.reports.domain.repositories.ReportRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportQueryServiceImpl implements ReportQueryService {

    private final ReportRepository reportRepository;

    public ReportQueryServiceImpl(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public List<Report> handle(GetAllReportsQuery query) {
        return reportRepository.findAll();
    }
}
