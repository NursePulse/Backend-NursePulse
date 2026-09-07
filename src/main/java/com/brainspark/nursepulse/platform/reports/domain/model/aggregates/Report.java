package com.brainspark.nursepulse.platform.reports.domain.model.aggregates;

import com.brainspark.nursepulse.platform.reports.domain.model.commands.CreateReportCommand;
import com.brainspark.nursepulse.platform.reports.domain.model.valueobjects.ReportStatus;
import com.brainspark.nursepulse.platform.reports.domain.model.valueobjects.ReportSummary;
import com.brainspark.nursepulse.platform.reports.domain.model.valueobjects.ReportType;
import com.brainspark.nursepulse.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Date;

@Getter
public class Report extends AbstractDomainAggregateRoot<Report> {

    @Setter
    private Long id;

    @Setter
    private Date createdAt;

    @Setter
    private ReportType type;

    @Setter
    private String title;

    @Setter
    private String generatedBy;

    @Setter
    private Instant startDate;

    @Setter
    private Instant endDate;

    @Setter
    private ReportStatus status;

    @Setter
    private ReportSummary summary;

    @Setter
    private String clinicalConclusion;

    public Report() {
        this.status = ReportStatus.PENDING;
    }

    public Report(CreateReportCommand command) {
        this.type = command.type();
        this.title = command.title();
        this.generatedBy = command.generatedBy();
        this.startDate = command.startDate();
        this.endDate = command.endDate();
        this.status = ReportStatus.COMPLETED;
        this.summary = command.summary();
        this.clinicalConclusion = command.clinicalConclusion();
    }
}
