package com.brainspark.nursepulse.platform.auditlogs.application.internal.queryservices;

import com.brainspark.nursepulse.platform.auditlogs.application.queryservices.AuditLogQueryService;
import com.brainspark.nursepulse.platform.auditlogs.domain.model.aggregates.AuditLog;
import com.brainspark.nursepulse.platform.auditlogs.domain.model.queries.GetAuditLogsQuery;
import com.brainspark.nursepulse.platform.auditlogs.domain.model.queries.GetAuditLogByIdQuery;
import com.brainspark.nursepulse.platform.auditlogs.domain.model.queries.GetPatientAuditTimelineQuery;
import com.brainspark.nursepulse.platform.auditlogs.domain.model.queries.GetEntityAuditHistoryQuery;
import com.brainspark.nursepulse.platform.auditlogs.infrastructure.persistence.jpa.assemblers.AuditLogSpecificationAssembler;
import com.brainspark.nursepulse.platform.auditlogs.infrastructure.persistence.jpa.repositories.AuditLogRepository;
import com.brainspark.nursepulse.platform.shared.application.result.Result;
import com.brainspark.nursepulse.platform.shared.application.result.ApplicationError;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogQueryServiceImpl implements AuditLogQueryService {
    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional(readOnly = true)
    public Result<Page<AuditLog>, ApplicationError> handle(GetAuditLogsQuery query){
        try{
            var specification = AuditLogSpecificationAssembler.fromQuery(query);
            var pageable = PageRequest.of(
                    query.pageOrDefault(),
                    query.sizeOrDefault(),
                    Sort.by(Sort.Direction.DESC, "performedAt")
            );
            Page<AuditLog> result = auditLogRepository.findAll(specification, pageable);
            return Result.success(result);
        } catch(Exception ex) {
            log.error("Unexpected error while querying audit log entries", ex);
            return Result.failure(ApplicationError.unexpected("AuditLogQueryService", ex.getMessage())
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Result<AuditLog, ApplicationError> handle(GetAuditLogByIdQuery query){
        return auditLogRepository.findById(query.auditLogId()).<Result<AuditLog, ApplicationError>>map(Result::success)
                .orElseGet(() -> Result.failure(ApplicationError.notFound("AuditLog", query.auditLogId().toString()
                )));
    }

    @Override
    @Transactional(readOnly = true)
    public Result<List<AuditLog>, ApplicationError> handle(GetPatientAuditTimelineQuery query) {
        try {
            Specification<AuditLog> spec = AuditLogSpecificationAssembler.fromPatientTimelineQuery(query);
            List<AuditLog> events = auditLogRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "performedAt"));
            return Result.success(events);
        } catch (Exception ex) {
            log.error("Unexpected error while building patient audit timeline for patient id={}", query.patientId(), ex);
            return Result.failure(ApplicationError.unexpected("AuditLogQueryService", ex.getMessage()));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Result<List<AuditLog>, ApplicationError> handle (GetEntityAuditHistoryQuery query){
        try{
            Specification<AuditLog> spec = AuditLogSpecificationAssembler.fromEntityHistoryQuery(query);
            List<AuditLog> events  = auditLogRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "performedAt"));
            return Result.success(events);
        } catch (Exception ex){
            log.error("Unexpected error while building entity audit history for entityType={}, entityId={}", query.entityType(), query.entityId(), ex);
            return Result.failure(ApplicationError.unexpected("AuditLogQueryService", ex.getMessage()));
        }
    }
}
