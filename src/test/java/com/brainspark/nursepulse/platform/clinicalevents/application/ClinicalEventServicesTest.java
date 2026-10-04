package com.brainspark.nursepulse.platform.clinicalevents.application;

import com.brainspark.nursepulse.platform.clinicalevents.application.internal.commandservices.ClinicalEventCommandServiceImpl;
import com.brainspark.nursepulse.platform.clinicalevents.application.internal.queryservices.ClinicalEventQueryServiceImpl;
import com.brainspark.nursepulse.platform.clinicalevents.domain.model.aggregates.ClinicalEvent;
import com.brainspark.nursepulse.platform.clinicalevents.domain.model.commands.CreateClinicalEventCommand;
import com.brainspark.nursepulse.platform.clinicalevents.domain.model.queries.GetAllClinicalEventsQuery;
import com.brainspark.nursepulse.platform.clinicalevents.domain.model.queries.GetClinicalEventsByPatientIdQuery;
import com.brainspark.nursepulse.platform.clinicalevents.domain.model.valueobjects.ClinicalEventSeverity;
import com.brainspark.nursepulse.platform.clinicalevents.domain.model.valueobjects.ClinicalEventType;
import com.brainspark.nursepulse.platform.clinicalevents.domain.repositories.ClinicalEventRepository;
import com.brainspark.nursepulse.platform.shared.application.result.ApplicationError;
import com.brainspark.nursepulse.platform.shared.application.result.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** US-18 (record clinical event), US-19 (event history), TS-03 (clinical records API). */
class ClinicalEventServicesTest {

    private ClinicalEventRepository repository;
    private ClinicalEventCommandServiceImpl commands;
    private ClinicalEventQueryServiceImpl queries;

    @BeforeEach
    void setUp() {
        repository = mock(ClinicalEventRepository.class);
        commands = new ClinicalEventCommandServiceImpl(repository);
        queries = new ClinicalEventQueryServiceImpl(repository);
    }

    private CreateClinicalEventCommand command() {
        return new CreateClinicalEventCommand(5L, ClinicalEventType.EMERGENCY, ClinicalEventSeverity.HIGH,
                "Arritmia", "Episodio de taquicardia", "nurse.maria");
    }

    @Test
    void savesEventWithDescriptionResponsibleAndTimestamp() {
        when(repository.save(any(ClinicalEvent.class))).thenAnswer(call -> call.getArgument(0));

        var result = commands.handle(command());

        var event = (ClinicalEvent) assertInstanceOf(Result.Success.class, result).value();
        assertEquals("Episodio de taquicardia", event.getDescription());
        assertEquals("nurse.maria", event.getRegisteredBy());
        assertNotNull(event.getOccurredAt());
    }

    @Test
    void persistenceFailureBecomesBusinessRuleViolation() {
        when(repository.save(any())).thenThrow(new RuntimeException("db down"));

        var error = (ApplicationError) assertInstanceOf(Result.Failure.class, commands.handle(command())).error();

        assertEquals("BUSINESS_RULE_VIOLATION", error.code());
    }

    @Test
    void historyIsFilteredByPatient() {
        var event = new ClinicalEvent(command());
        when(repository.findByPatientId(5L)).thenReturn(List.of(event));
        when(repository.findAll()).thenReturn(List.of(event, new ClinicalEvent(command())));

        assertEquals(1, queries.handle(new GetClinicalEventsByPatientIdQuery(5L)).size());
        assertEquals(2, queries.handle(new GetAllClinicalEventsQuery()).size());
    }
}
