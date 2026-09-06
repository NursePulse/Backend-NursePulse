package com.brainspark.nursepulse.platform.vitalsigns.interfaces.rest;

import com.brainspark.nursepulse.platform.iam.interfaces.acl.IamContextFacade;
import com.brainspark.nursepulse.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import com.brainspark.nursepulse.platform.vitalsigns.application.commandservices.VitalSignRecordCommandService;
import com.brainspark.nursepulse.platform.vitalsigns.application.queryservices.VitalSignRecordQueryService;
import com.brainspark.nursepulse.platform.vitalsigns.domain.model.queries.GetLatestVitalSignRecordByPatientIdQuery;
import com.brainspark.nursepulse.platform.vitalsigns.domain.model.queries.GetVitalSignRecordByIdQuery;
import com.brainspark.nursepulse.platform.vitalsigns.domain.model.queries.GetVitalSignRecordsByPatientIdQuery;
import com.brainspark.nursepulse.platform.vitalsigns.interfaces.rest.resources.CreateVitalSignRecordResource;
import com.brainspark.nursepulse.platform.vitalsigns.interfaces.rest.transform.CreateVitalSignRecordCommandFromResourceAssembler;
import com.brainspark.nursepulse.platform.vitalsigns.interfaces.rest.transform.VitalSignRecordResourceFromEntityAssembler;
import com.brainspark.nursepulse.platform.vitalsigns.domain.model.queries.GetAllVitalSignRecordsQuery;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/vital-sign-records", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Vital Sign Records", description = "Vital sign record management endpoints")
public class VitalSignRecordsController {

    private final VitalSignRecordCommandService vitalSignRecordCommandService;
    private final VitalSignRecordQueryService vitalSignRecordQueryService;
    private final IamContextFacade iamContextFacade;


    public VitalSignRecordsController(
            VitalSignRecordCommandService vitalSignRecordCommandService,
            VitalSignRecordQueryService vitalSignRecordQueryService,
            IamContextFacade iamContextFacade
    ) {
        this.vitalSignRecordCommandService = vitalSignRecordCommandService;
        this.vitalSignRecordQueryService = vitalSignRecordQueryService;
        this.iamContextFacade = iamContextFacade;
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createVitalSignRecord(
            @RequestBody @Valid CreateVitalSignRecordResource resource,
            Authentication authentication
    ) {
        var nurseId = iamContextFacade.fetchUserIdByUsername(authentication.getName());
        var command = CreateVitalSignRecordCommandFromResourceAssembler.toCommandFromResource(resource, nurseId);
        var result = vitalSignRecordCommandService.handle(command);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                VitalSignRecordResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<?> getAllVitalSignRecords() {
        var query = new GetAllVitalSignRecordsQuery();
        var result = vitalSignRecordQueryService.handle(query);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                vitalSignRecords -> vitalSignRecords.stream()
                        .map(VitalSignRecordResourceFromEntityAssembler::toResourceFromEntity)
                        .toList(),
                HttpStatus.OK
        );
    }

    @GetMapping("/patients/{patientId}")
    public ResponseEntity<?> getVitalSignRecordsByPatientId(
            @PathVariable Long patientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        var query = new GetVitalSignRecordsByPatientIdQuery(patientId, from, to);
        var result = vitalSignRecordQueryService.handle(query);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                vitalSignRecords -> vitalSignRecords.stream()
                        .map(VitalSignRecordResourceFromEntityAssembler::toResourceFromEntity)
                        .toList(),
                HttpStatus.OK
        );
    }

    @GetMapping("/patients/{patientId}/latest")
    public ResponseEntity<?> getLatestVitalSignRecordByPatientId(@PathVariable Long patientId) {
        var query = new GetLatestVitalSignRecordByPatientIdQuery(patientId);
        var result = vitalSignRecordQueryService.handle(query);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                VitalSignRecordResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK
        );
    }

    @GetMapping("/{vitalSignRecordId}")
    public ResponseEntity<?> getVitalSignRecordById(@PathVariable Long vitalSignRecordId) {
        var query = new GetVitalSignRecordByIdQuery(vitalSignRecordId);
        var result = vitalSignRecordQueryService.handle(query);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                VitalSignRecordResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK
        );
    }
}