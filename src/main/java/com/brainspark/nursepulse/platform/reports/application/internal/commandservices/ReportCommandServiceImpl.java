package com.brainspark.nursepulse.platform.reports.application.internal.commandservices;

import com.brainspark.nursepulse.platform.reports.application.commandservices.ReportCommandService;
import com.brainspark.nursepulse.platform.reports.domain.model.aggregates.Report;
import com.brainspark.nursepulse.platform.reports.domain.model.commands.CreateReportCommand;
import com.brainspark.nursepulse.platform.reports.domain.repositories.ReportRepository;
import com.brainspark.nursepulse.platform.shared.application.result.ApplicationError;
import com.brainspark.nursepulse.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

@Service
public class ReportCommandServiceImpl implements ReportCommandService {

    private final ReportRepository reportRepository;

    public ReportCommandServiceImpl(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public Result<Report, ApplicationError> handle(CreateReportCommand command) {
        try {
            var report = new Report(command);
            report = reportRepository.save(report);
            return Result.success(report);
        } catch (RuntimeException exception) {
            return Result.failure(ApplicationError.businessRuleViolation(
                    "create report",
                    exception.getMessage()
            ));
        }
    }
}
