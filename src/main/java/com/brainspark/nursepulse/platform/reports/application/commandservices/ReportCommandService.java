package com.brainspark.nursepulse.platform.reports.application.commandservices;

import com.brainspark.nursepulse.platform.reports.domain.model.aggregates.Report;
import com.brainspark.nursepulse.platform.reports.domain.model.commands.CreateReportCommand;
import com.brainspark.nursepulse.platform.shared.application.result.ApplicationError;
import com.brainspark.nursepulse.platform.shared.application.result.Result;

public interface ReportCommandService {
    Result<Report, ApplicationError> handle(CreateReportCommand command);
}
