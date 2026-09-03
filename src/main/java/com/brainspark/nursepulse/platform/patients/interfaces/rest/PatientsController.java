package com.brainspark.nursepulse.platform.patients.interfaces.rest;

import com.brainspark.nursepulse.platform.patients.application.commandservices.PatientCommandService;
import com.brainspark.nursepulse.platform.patients.application.queryservices.PatientQueryService;
import com.brainspark.nursepulse.platform.patients.domain.model.commands.DeletePatientCommand;
import com.brainspark.nursepulse.platform.patients.domain.model.queries.GetAllPatientsQuery;
import com.brainspark.nursepulse.platform.patients.domain.model.queries.GetPatientByIdQuery;
import com.brainspark.nursepulse.platform.patients.interfaces.rest.resources.CreatePatientResource;
import com.brainspark.nursepulse.platform.patients.interfaces.rest.resources.UpdatePatientResource;
import com.brainspark.nursepulse.platform.patients.interfaces.rest.transform.CreatePatientCommandFromResourceAssembler;
import com.brainspark.nursepulse.platform.patients.interfaces.rest.transform.PatientResourceFromEntityAssembler;
import com.brainspark.nursepulse.platform.patients.interfaces.rest.transform.UpdatePatientCommandFromResourceAssembler;
import com.brainspark.nursepulse.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/patients", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Patients", description = "Patient management endpoints")
public class PatientsController {

    private final PatientCommandService patientCommandService;
    private final PatientQueryService patientQueryService;

    public PatientsController(
            PatientCommandService patientCommandService,
            PatientQueryService patientQueryService
    ) {
        this.patientCommandService = patientCommandService;
        this.patientQueryService = patientQueryService;
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createPatient(@RequestBody @Valid CreatePatientResource resource) {
        var command = CreatePatientCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = patientCommandService.handle(command);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                PatientResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<?> getAllPatients() {
        var query = new GetAllPatientsQuery();
        var result = patientQueryService.handle(query);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                patients -> patients.stream()
                        .map(PatientResourceFromEntityAssembler::toResourceFromEntity)
                        .toList(),
                HttpStatus.OK
        );
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<?> getPatientById(@PathVariable Long patientId) {
        var query = new GetPatientByIdQuery(patientId);
        var result = patientQueryService.handle(query);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                PatientResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK
        );
    }

    @PutMapping(value = "/{patientId}", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updatePatient(
            @PathVariable Long patientId,
            @RequestBody @Valid UpdatePatientResource resource
    ) {
        var command = UpdatePatientCommandFromResourceAssembler.toCommandFromResource(patientId, resource);
        var result = patientCommandService.handle(command);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                PatientResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{patientId}")
    public ResponseEntity<?> deletePatient(@PathVariable Long patientId) {
        var command = new DeletePatientCommand(patientId);
        var result = patientCommandService.handle(command);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                ignored -> null,
                HttpStatus.NO_CONTENT
        );
    }
}