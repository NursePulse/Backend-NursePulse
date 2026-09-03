package com.brainspark.nursepulse.platform.auditlogs.application.internal.commandservices;

import com.brainspark.nursepulse.platform.auditlogs.domain.model.aggregates.AuditLog;
import com.brainspark.nursepulse.platform.auditlogs.domain.model.commands.CreateAuditLogCommand;
import com.brainspark.nursepulse.platform.auditlogs.domain.services.AuditLogCommandService;
import com.brainspark.nursepulse.platform.auditlogs.infrastructure.persistence.jpa.repositories.AuditLogRepository;
import com.brainspark.nursepulse.platform.shared.application.result.ApplicationError;
import com.brainspark.nursepulse.platform.shared.application.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Command service responsible for creating and persisting {@link AuditLog} aggregates.
 *
 * This application-layer service implements {@link AuditLogCommandService} and
 * orchestrates the audit log creation workflow:
 * <ol>
 *   <li>Creates a new {@link AuditLog} aggregate using the domain factory method.</li>
 *   <li>Persists the aggregate through {@link AuditLogRepository}.</li>
 *   <li>Logs successful creation details.</li>
 *   <li>Returns the result wrapped in a {@link Result} object.</li>
 * </ol>
 *
 * All operations are executed within a transactional boundary to ensure
 * consistency between aggregate creation and persistence.
 *
 * Any unexpected exception is logged and converted into an {@link ApplicationError} failure result.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogCommandServiceImpl implements AuditLogCommandService {

    /**
     * Repository used to persist audit log aggregates.
     */
    private final AuditLogRepository auditLogRepository;

    /**
     * Creates and persists a new audit log entry.
     *
     * The method delegates aggregate instantiation to {@link AuditLog#create(CreateAuditLogCommand)}, persists the resulting
     * aggregate, and returns the persisted entity wrapped in a successful
     * {@link Result}.
     *
     * If an unexpected error occurs during creation or persistence,
     * the exception is logged and a failure {@link Result} containing an {@link ApplicationError} is returned.
     *
     * @param command command containing the information required to create
     *                an audit log entry
     * @return a successful result containing the persisted {@link AuditLog},
     *         or a failure result containing an {@link ApplicationError}
     */
    @Override
    @Transactional
    public Result<AuditLog, ApplicationError> handle(CreateAuditLogCommand command) {
        try {
            AuditLog entry = AuditLog.create(command);
            AuditLog saved = auditLogRepository.save(entry);

            log.info(
                    "Audit log entry created: id={}, action={}, entity={}/{}",
                    saved.getId(),
                    saved.getActionType(),
                    saved.getEntityType(),
                    saved.getEntityId()
            );

            return Result.success(saved);
        } catch (Exception ex) {
            log.error("Unexpected error while persisting audit log entry", ex);

            return Result.failure(
                    ApplicationError.unexpected(
                            "AuditLogCommandService",
                            ex.getMessage()
                    )
            );
        }
    }
}