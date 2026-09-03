package com.brainspark.nursepulse.platform.auditlogs.interfaces.REST;

import com.brainspark.nursepulse.platform.auditlogs.domain.model.queries.GetEntityAuditHistoryQuery;
import com.brainspark.nursepulse.platform.auditlogs.domain.services.AuditLogCommandService;
import com.brainspark.nursepulse.platform.auditlogs.domain.model.queries.GetAuditLogsQuery;
import com.brainspark.nursepulse.platform.auditlogs.domain.model.queries.GetAuditLogByIdQuery;
import com.brainspark.nursepulse.platform.auditlogs.domain.model.valueobjects.AuditActionType;
import com.brainspark.nursepulse.platform.auditlogs.domain.model.valueobjects.AuditedEntityType;
import com.brainspark.nursepulse.platform.auditlogs.interfaces.REST.resources.*;
import com.brainspark.nursepulse.platform.auditlogs.interfaces.REST.transform.*;
import com.brainspark.nursepulse.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import com.brainspark.nursepulse.platform.auditlogs.application.queryservices.AuditLogQueryService;
import com.brainspark.nursepulse.platform.auditlogs.domain.model.aggregates.AuditLog;
import com.brainspark.nursepulse.platform.auditlogs.domain.model.queries.GetPatientAuditTimelineQuery;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

/**
 * REST controller for the auditlogs bounded context.
 */
@RestController
@RequestMapping(value = "/api/v1/audit-logs", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Audit Logs", description = "Clinical traceability endpoints (append-only audit trail)")
public class AuditLogsController {

    private final AuditLogCommandService auditLogCommandService;
    private final AuditLogQueryService auditLogQueryService;

    @PostMapping
    @Operation(
            summary = "Create an audit log entry",
            description = "Persists a new, immutable audit log entry for a clinical action."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Audit log entry created",
                    content = @Content(schema = @Schema(implementation = AuditLogResource.class))),
            @ApiResponse(responseCode = "400", description = "Validation error, missing or invalid field(s)",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content)
    })
    public ResponseEntity<?> createAuditLog(
            @Valid @RequestBody CreateAuditLogResource resource,
            Authentication authentication
    ) {
        var command = CreateAuditLogCommandFromResourceAssembler.toCommandFromResource(
                resource,
                authentication.getName()
        );
        var result  = auditLogCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                AuditLogResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED
        );
    }

    @GetMapping
    @Operation(
            summary = "List audit log entries",
            description = "Returns a paginated, chronologically ordered list of audit log " +
                    "entries. All filters are optional and can be combined freely. This endpoint is designed " +
                    "to be reusable for a patient timeline or an entity timeline " +
                    "(filter by entityType/entityId)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paginated list of audit log entries",
                    content = @Content(schema = @Schema(implementation = PagedResult.class))),
            @ApiResponse(responseCode = "400", description = "Invalid filter or pagination parameters",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content)
    })
    public ResponseEntity<?> getAuditLogs(
            @Parameter(description = "Filter by patient identifier", example = "10")
            @RequestParam(required = false) @Nullable Long patientId,

            @Parameter(description = "Filter by audited entity type", example = "VITAL_SIGNS")
            @RequestParam(required = false) @Nullable AuditedEntityType entityType,

            @Parameter(description = "Filter by audited entity identifier", example = "9fr4l2")
            @RequestParam(required = false) @Nullable String entityId,

            @Parameter(description = "Filter by action type", example = "CREATE")
            @RequestParam(required = false) @Nullable AuditActionType actionType,

            @Parameter(description = "Filter by the staff member who performed the action", example = "nurse-user-31")
            @RequestParam(required = false) @Nullable String performedBy,

            @Parameter(description = "Inclusive lower bound for performedAt", example = "2026-01-01T00:00:00Z")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Nullable Instant from,

            @Parameter(description = "Inclusive upper bound for performedAt", example = "2026-12-31T23:59:59Z")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Nullable Instant to,

            @Parameter(description = "Zero based page index", example = "0")
            @RequestParam(required = false, defaultValue = "0") Integer page,

            @Parameter(description = "Page size (max 200)", example = "20")
            @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        var filter = new AuditLogFilterResource(
                patientId, entityType, entityId, actionType, performedBy, from, to, page, size
        );
        var result = auditLogQueryService.handle(toQueryFromFilter(filter));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                resultPage -> PagedResult.from(resultPage, AuditLogResourceFromEntityAssembler::toResourceFromEntity),
                HttpStatus.OK
        );
    }

    @GetMapping("/{auditLogId}")
    @Operation(
            summary = "Get audit log entry detail",
            description = "Returns the full detail of a single audit log entry by its identifier. "
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Audit log entry detail",
                    content = @Content(schema = @Schema(implementation = AuditLogDetailResource.class))),
            @ApiResponse(responseCode = "404", description = "Audit log entry not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content)
    })
    public ResponseEntity<?> getAuditLogById(
            @Parameter(description = "Identifier of the audit log entry", example = "1")
            @PathVariable Long auditLogId
    ) {
        var query = new GetAuditLogByIdQuery(auditLogId);
        var result = auditLogQueryService.handle(query);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                AuditLogDetailResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK
        );
    }

    @GetMapping(value= "/patients/{patientId}/timeline", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary="Get patient audit timeline", description = "Returns the full chronological audit trail for a specific patient. " +
            "The events are ordered oldest to newest so a timeline component can render them top-to-bottom without a client-side sort. " +
            "Use the optional 'from'/'to' parameters to restrict the timeline to any logical clinical period. " +
            "When a patient has no recorded audit events in our service, an empty list is a valid response")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Patient audit timeline",
                    content = @Content(schema = @Schema(implementation = AuditLogTimelineResource.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content)
    })
    public ResponseEntity<?> getPatientAuditTimeline(
            @Parameter(description = "Patient identifier", example = "15")
            @PathVariable Long patientId,

            @Parameter(description = "Inclusive lower bound for performedAt (use format ISO-8601)", example = "2026-01-01T00:00:00Z")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Nullable Instant from,

            @Parameter(description = "Inclusive upper bound for performedAt (use format ISO-8601)", example = "2026-12-31T23:59:59Z")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Nullable Instant to
    ) {
        var query = new GetPatientAuditTimelineQuery(patientId, from, to);
        var result = auditLogQueryService.handle(query);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                events -> toTimelineResource(patientId, from, to, events),
                HttpStatus.OK
        );
    }

    @GetMapping("/entities/{entityType}/{entityId}")
    @Operation(summary ="Get entity audit history", description="Returns the full chronological audit history for a specific clinical entity " +
            "(examples: a vital sign record, an SBAR handover, a clinical event, etc). " +
            "Events are ordered oldest to newest. An empty list is a valid response only whe no audit event exit for the entity."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entity audit history (may be empty)",
                    content = @Content(schema = @Schema(implementation = AuditLogHistoryResource.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content)
    })
    public ResponseEntity<?> getEntityAuditHistory(
            @Parameter(description = "Type of the clinical entity", example= "VITAL_SIGNS")
            @PathVariable AuditedEntityType entityType,

            @Parameter(description="Identifier of the clinical entity instance", example="5t6uy2")
            @PathVariable String entityId
    ){
        var query = new GetEntityAuditHistoryQuery(entityType, entityId);
        var result = auditLogQueryService.handle(query);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                events -> toHistoryResource(entityType, entityId, events),
                HttpStatus.OK
        );
    }

    private GetAuditLogsQuery toQueryFromFilter(AuditLogFilterResource filter) {
        return new GetAuditLogsQuery(
                filter.patientId(),
                filter.entityType(),
                filter.entityId(),
                filter.actionType(),
                filter.performedBy(),
                filter.from(),
                filter.to(),
                filter.page(),
                filter.size()
        );
    }

    private AuditLogTimelineResource toTimelineResource(
            Long patientId,
            @Nullable
            Instant from,
            @Nullable
            Instant to,
            List<AuditLog> events
    ){
        var items = events.stream().map(AuditLogTimelineItemResourceFromEntityAssembler::toResourceFromEntity).toList();
        return new AuditLogTimelineResource(patientId, from, to, items.size(), items);

    }

    private AuditLogHistoryResource toHistoryResource(
            AuditedEntityType entityType,
            String entityId,
            List<AuditLog> events
    ){
        var items = events.stream().map(AuditLogHistoryItemResourceFromEntityAssembler::toResourceFromEntity).toList();
        return new AuditLogHistoryResource(entityType, entityId, items.size(), items);
    }
}
