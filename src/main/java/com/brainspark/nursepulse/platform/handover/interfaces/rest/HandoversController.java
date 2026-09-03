package com.brainspark.nursepulse.platform.handover.interfaces.rest;

import com.brainspark.nursepulse.platform.handover.application.commandservices.HandoverCommandService;
import com.brainspark.nursepulse.platform.handover.application.queryservices.HandoverQueryService;
import com.brainspark.nursepulse.platform.handover.domain.model.queries.GetHandoverByIdQuery;
import com.brainspark.nursepulse.platform.handover.interfaces.rest.resources.CreateHandoverResource;
import com.brainspark.nursepulse.platform.handover.interfaces.rest.resources.HandoverResource;
import com.brainspark.nursepulse.platform.handover.interfaces.rest.transform.CreateHandoverCommandFromResourceAssembler;
import com.brainspark.nursepulse.platform.handover.interfaces.rest.transform.HandoverResourceFromEntityAssembler;
import com.brainspark.nursepulse.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/handovers", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Handovers", description = "Handover management endpoints")
public class HandoversController {
    private final HandoverCommandService handoverCommandService;
    private final HandoverQueryService handoverQueryService;

    public HandoversController(HandoverCommandService handoverCommandService, HandoverQueryService handoverQueryService) {
        this.handoverCommandService = handoverCommandService;
        this.handoverQueryService = handoverQueryService;
    }

    @PostMapping
    @Operation(summary = "Create a new handover", description = "Creates a new handover with title and description.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Handover created successfully",
                    content = @Content(schema = @Schema(implementation = HandoverResource.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Handover with the same title already exists")
    })
    public ResponseEntity<?> createHandover(@RequestBody CreateHandoverResource resource) {
        var createHandoverCommand = CreateHandoverCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = handoverCommandService.handle(createHandoverCommand);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                id -> id,
                HttpStatus.CREATED
        );
    }

    @GetMapping("/patients/{patientId}")
    @Operation(summary = "Get handovers by patient ID", description = "Gets all handovers for a specific patient, with optional date filtering.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Handovers retrieved successfully",
                    content = @Content(schema = @Schema(implementation = HandoverResource.class))
            )
    })
    public ResponseEntity<java.util.List<HandoverResource>> getHandoversByPatientId(
            @PathVariable Long patientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate
    ) {
        var query = new com.brainspark.nursepulse.platform.handover.domain.model.queries.GetAllHandoversByPatientIdQuery(patientId, startDate, endDate);
        var handovers = handoverQueryService.handle(query);
        var resources = handovers.stream()
                .map(HandoverResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{handoverId}")
    @Operation(summary = "Get specific handover details", description = "Gets the complete information of a particular SBAR handover.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Handover details retrieved successfully",
                    content = @Content(schema = @Schema(implementation = com.brainspark.nursepulse.platform.handover.interfaces.rest.resources.HandoverDetailedResource.class))
            ),
            @ApiResponse(responseCode = "404", description = "Handover not found")
    })
    public ResponseEntity<com.brainspark.nursepulse.platform.handover.interfaces.rest.resources.HandoverDetailedResource> getHandoverById(
            @PathVariable Long handoverId
    ) {
        var query = new GetHandoverByIdQuery(handoverId);
        var handover = handoverQueryService.handle(query);

        if (handover.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var resource = com.brainspark.nursepulse.platform.handover.interfaces.rest.transform.HandoverDetailedResourceFromEntityAssembler.toResourceFromEntity(handover.get());
        return ResponseEntity.ok(resource);
    }

    @PatchMapping("/{handoverId}/acknowledge")
    @Operation(summary = "Acknowledge a handover", description = "Allows the incoming nurse to confirm they have read and understood the handover.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Handover acknowledged successfully",
                    content = @Content(schema = @Schema(implementation = HandoverResource.class))
            ),
            @ApiResponse(responseCode = "404", description = "Handover not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<?> acknowledgeHandover(
            @PathVariable Long handoverId,
            @RequestBody com.brainspark.nursepulse.platform.handover.interfaces.rest.resources.AcknowledgeHandoverResource resource
    ) {
        var command = com.brainspark.nursepulse.platform.handover.interfaces.rest.transform.AcknowledgeHandoverCommandFromResourceAssembler.toCommandFromResource(handoverId, resource);
        var result = handoverCommandService.handle(command);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                HandoverResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK
        );
    }
}