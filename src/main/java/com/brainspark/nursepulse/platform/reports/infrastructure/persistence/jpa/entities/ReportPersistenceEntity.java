package com.brainspark.nursepulse.platform.reports.infrastructure.persistence.jpa.entities;

import com.brainspark.nursepulse.platform.reports.domain.model.valueobjects.ReportStatus;
import com.brainspark.nursepulse.platform.reports.domain.model.valueobjects.ReportType;
import com.brainspark.nursepulse.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "reports")
@Getter
@Setter
@NoArgsConstructor
public class ReportPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportType type;

    @Column(nullable = false)
    private String title;

    @Column(name = "generated_by", nullable = false)
    private String generatedBy;

    @Column(name = "start_date", nullable = false)
    private Instant startDate;

    @Column(name = "end_date", nullable = false)
    private Instant endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status;

    @Embedded
    private ReportSummaryEmbeddable summary;

    @Column(name = "clinical_conclusion", length = 2000)
    private String clinicalConclusion;
}
