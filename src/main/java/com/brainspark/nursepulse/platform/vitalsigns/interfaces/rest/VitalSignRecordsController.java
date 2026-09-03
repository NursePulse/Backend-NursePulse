package com.brainspark.nursepulse.platform.vitalsigns.interfaces.rest;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/vital-sign-records", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Vital Sign Records", description = "Vital sign record management endpoints")
public class VitalSignRecordsController {

    private final VitalSignRecordCommandService vitalSignRecordCommandService;
    private final VitalSignRecordQueryService vitalSignRecordQueryService;


    public VitalSignRecordsController(
            VitalSignRecordCommandService vitalSignRecordCommandService,
            VitalSignRecordQueryService vitalSignRecordQueryService
    ) {
        this.vitalSignRecordCommandService = vitalSignRecordCommandService;
        this.vitalSignRecordQueryService = vitalSignRecordQueryService;
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createVitalSignRecord(@RequestBody @Valid CreateVitalSignRecordResource resource) {
        var command = CreateVitalSignRecordCommandFromResourceAssembler.toCommandFromResource(resource);
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
    public ResponseEntity<?> getVitalSignRecordsByPatientId(@PathVariable Long patientId) {
        var query = new GetVitalSignRecordsByPatientIdQuery(patientId);
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