package com.brainspark.nursepulse.platform.criticalevents.interfaces.rest;

import com.brainspark.nursepulse.platform.criticalevents.application.commandservices.AlertCommandService;
import com.brainspark.nursepulse.platform.criticalevents.application.queryservices.AlertQueryService;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.queries.GetAlertByIdQuery;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.queries.GetAlertsByPatientIdQuery;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.queries.GetAllAlertsQuery;
import com.brainspark.nursepulse.platform.criticalevents.interfaces.rest.resources.AttendAlertResource;
import com.brainspark.nursepulse.platform.criticalevents.interfaces.rest.resources.CloseAlertResource;
import com.brainspark.nursepulse.platform.criticalevents.interfaces.rest.resources.CreateAlertResource;
import com.brainspark.nursepulse.platform.criticalevents.interfaces.rest.transform.AlertResourceFromEntityAssembler;
import com.brainspark.nursepulse.platform.criticalevents.interfaces.rest.transform.AttendAlertCommandFromResourceAssembler;
import com.brainspark.nursepulse.platform.criticalevents.interfaces.rest.transform.CloseAlertCommandFromResourceAssembler;
import com.brainspark.nursepulse.platform.criticalevents.interfaces.rest.transform.CreateAlertCommandFromResourceAssembler;
import com.brainspark.nursepulse.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/alerts", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Alerts", description = "Critical event alert management endpoints")
public class AlertsController {

    private final AlertCommandService alertCommandService;
    private final AlertQueryService alertQueryService;

    public AlertsController(
            AlertCommandService alertCommandService,
            AlertQueryService alertQueryService
    ) {
        this.alertCommandService = alertCommandService;
        this.alertQueryService = alertQueryService;
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createAlert(@RequestBody @Valid CreateAlertResource resource) {
        var command = CreateAlertCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = alertCommandService.handle(command);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                AlertResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<?> getAllAlerts() {
        var query = new GetAllAlertsQuery();
        var result = alertQueryService.handle(query);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                alerts -> alerts.stream()
                        .map(AlertResourceFromEntityAssembler::toResourceFromEntity)
                        .toList(),
                HttpStatus.OK
        );
    }

    @GetMapping("/patients/{patientId}")
    public ResponseEntity<?> getAlertsByPatientId(@PathVariable Long patientId) {
        var query = new GetAlertsByPatientIdQuery(patientId);
        var result = alertQueryService.handle(query);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                alerts -> alerts.stream()
                        .map(AlertResourceFromEntityAssembler::toResourceFromEntity)
                        .toList(),
                HttpStatus.OK
        );
    }

    @GetMapping("/{alertId}")
    public ResponseEntity<?> getAlertById(@PathVariable Long alertId) {
        var query = new GetAlertByIdQuery(alertId);
        var result = alertQueryService.handle(query);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                AlertResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK
        );
    }

    @PatchMapping(value = "/{alertId}/attend", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> attendAlert(
            @PathVariable Long alertId,
            @RequestBody @Valid AttendAlertResource resource
    ) {
        var command = AttendAlertCommandFromResourceAssembler.toCommandFromResource(alertId, resource);
        var result = alertCommandService.handle(command);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                AlertResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK
        );
    }

    @PatchMapping(value = "/{alertId}/close", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> closeAlert(
            @PathVariable Long alertId,
            @RequestBody @Valid CloseAlertResource resource
    ) {
        var command = CloseAlertCommandFromResourceAssembler.toCommandFromResource(alertId, resource);
        var result = alertCommandService.handle(command);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                AlertResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK
        );
    }
}
