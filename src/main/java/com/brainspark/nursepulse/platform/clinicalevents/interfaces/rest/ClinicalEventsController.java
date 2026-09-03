package com.brainspark.nursepulse.platform.clinicalevents.interfaces.rest;

import com.brainspark.nursepulse.platform.clinicalevents.application.commandservices.ClinicalEventCommandService;
import com.brainspark.nursepulse.platform.clinicalevents.application.queryservices.ClinicalEventQueryService;
import com.brainspark.nursepulse.platform.clinicalevents.domain.model.queries.GetAllClinicalEventsQuery;
import com.brainspark.nursepulse.platform.clinicalevents.domain.model.queries.GetClinicalEventsByPatientIdQuery;
import com.brainspark.nursepulse.platform.clinicalevents.interfaces.rest.resources.ClinicalEventResource;
import com.brainspark.nursepulse.platform.clinicalevents.interfaces.rest.resources.CreateClinicalEventResource;
import com.brainspark.nursepulse.platform.clinicalevents.interfaces.rest.transform.ClinicalEventResourceFromEntityAssembler;
import com.brainspark.nursepulse.platform.clinicalevents.interfaces.rest.transform.CreateClinicalEventCommandFromResourceAssembler;
import com.brainspark.nursepulse.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller that exposes clinical event resources.
 */
@RestController
@RequestMapping(value = "/api/v1/clinical-events", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Clinical Events", description = "Operational clinical event endpoints")
public class ClinicalEventsController {

    private final ClinicalEventCommandService clinicalEventCommandService;
    private final ClinicalEventQueryService clinicalEventQueryService;

    public ClinicalEventsController(
            ClinicalEventCommandService clinicalEventCommandService,
            ClinicalEventQueryService clinicalEventQueryService) {
        this.clinicalEventCommandService = clinicalEventCommandService;
        this.clinicalEventQueryService = clinicalEventQueryService;
    }

    /**
     * Registers a clinical event.
     *
     * @param resource clinical event registration request
     * @return created clinical event resource
     * @see ClinicalEventResource
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Register a clinical event",
        description = "Registers an operational clinical event for a patient. The authenticated clinical staff member is recorded as the author.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "201",
                description = "Clinical event registered successfully",
                content = @Content(schema = @Schema(implementation = ClinicalEventResource.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid event data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
    })
    public ResponseEntity<?> createClinicalEvent(@Valid @RequestBody CreateClinicalEventResource resource) {
        var registeredBy = SecurityContextHolder.getContext().getAuthentication().getName();
        var command = CreateClinicalEventCommandFromResourceAssembler.toCommandFromResource(resource, registeredBy);
        var result = clinicalEventCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                ClinicalEventResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED
        );
    }

    /**
     * Retrieves all clinical events.
     *
     * @return list of clinical event resources
     * @see ClinicalEventResource
     */
    @GetMapping
    @Operation(
        summary = "Get all clinical events",
        description = "Retrieves all registered clinical events.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200",
                description = "Clinical events retrieved successfully",
                content = @Content(schema = @Schema(implementation = ClinicalEventResource.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
    })
    public ResponseEntity<List<ClinicalEventResource>> getAllClinicalEvents() {
        var clinicalEvents = clinicalEventQueryService.handle(new GetAllClinicalEventsQuery());
        var resources = clinicalEvents.stream()
                .map(ClinicalEventResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    /**
     * Retrieves the clinical events of a patient.
     *
     * @param patientId patient identifier
     * @return list of clinical event resources for the patient
     * @see ClinicalEventResource
     */
    @GetMapping("/patients/{patientId}")
    @Operation(
        summary = "Get clinical events by patient",
        description = "Retrieves the clinical events registered for a specific patient.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200",
                description = "Clinical events retrieved successfully",
                content = @Content(schema = @Schema(implementation = ClinicalEventResource.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
    })
    public ResponseEntity<List<ClinicalEventResource>> getClinicalEventsByPatientId(
            @PathVariable
            @Parameter(description = "Patient identifier", example = "1", required = true)
            Long patientId
    ) {
        var clinicalEvents = clinicalEventQueryService.handle(new GetClinicalEventsByPatientIdQuery(patientId));
        var resources = clinicalEvents.stream()
                .map(ClinicalEventResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }
}
