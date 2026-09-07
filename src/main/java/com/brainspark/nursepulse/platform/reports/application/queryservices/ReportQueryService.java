package com.brainspark.nursepulse.platform.reports.application.queryservices;

import com.brainspark.nursepulse.platform.reports.domain.model.aggregates.Report;
import com.brainspark.nursepulse.platform.reports.domain.model.queries.GetAllReportsQuery;

import java.util.List;

public interface ReportQueryService {
    List<Report> handle(GetAllReportsQuery query);
}
