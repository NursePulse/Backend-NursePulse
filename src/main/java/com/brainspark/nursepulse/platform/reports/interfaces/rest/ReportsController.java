package com.brainspark.nursepulse.platform.reports.interfaces.rest;

import com.brainspark.nursepulse.platform.reports.application.commandservices.ReportCommandService;
import com.brainspark.nursepulse.platform.reports.application.queryservices.ReportQueryService;
import com.brainspark.nursepulse.platform.reports.domain.model.queries.GetAllReportsQuery;
import com.brainspark.nursepulse.platform.reports.interfaces.rest.resources.CreateReportResource;
import com.brainspark.nursepulse.platform.reports.interfaces.rest.resources.ReportResource;
import com.brainspark.nursepulse.platform.reports.interfaces.rest.transform.CreateReportCommandFromResourceAssembler;
import com.brainspark.nursepulse.platform.reports.interfaces.rest.transform.ReportResourceFromEntityAssembler;
import com.brainspark.nursepulse.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/reports", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Reports", description = "Consolidated clinical report endpoints")
public class ReportsController {

    private final ReportCommandService reportCommandService;
    private final ReportQueryService reportQueryService;

    public ReportsController(ReportCommandService reportCommandService, ReportQueryService reportQueryService) {
        this.reportCommandService = reportCommandService;
        this.reportQueryService = reportQueryService;
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Persist a consolidated clinical report",
            description = "Persists a report already consolidated by the client from live clinical data. " +
                    "The reporting user is derived from the authenticated JWT."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Report persisted",
                    content = @Content(schema = @Schema(implementation = ReportResource.class))),
            @ApiResponse(responseCode = "400", description = "Validation error, missing or invalid field(s)",
                    content = @Content)
    })
    public ResponseEntity<?> createReport(
            @Valid @RequestBody CreateReportResource resource,
            Authentication authentication
    ) {
        var command = CreateReportCommandFromResourceAssembler.toCommandFromResource(resource, authentication.getName());
        var result = reportCommandService.handle(command);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                ReportResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED
        );
    }

    @GetMapping
    @Operation(
            summary = "List persisted reports",
            description = "Returns every persisted report, most recently generated first."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of reports",
                    content = @Content(schema = @Schema(implementation = ReportResource.class)))
    })
    public ResponseEntity<?> getAllReports() {
        var reports = reportQueryService.handle(new GetAllReportsQuery());
        var resources = reports.stream().map(ReportResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(resources);
    }
}
