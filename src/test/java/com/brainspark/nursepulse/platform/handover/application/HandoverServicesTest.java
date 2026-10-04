package com.brainspark.nursepulse.platform.handover.application;

import com.brainspark.nursepulse.platform.handover.application.internal.commandservices.HandoverCommandServicesImpl;
import com.brainspark.nursepulse.platform.handover.application.internal.queryservices.HandoverQueryServiceImpl;
import com.brainspark.nursepulse.platform.handover.domain.model.aggregates.Handover;
import com.brainspark.nursepulse.platform.handover.domain.model.commands.AcknowledgeHandoverCommand;
import com.brainspark.nursepulse.platform.handover.domain.model.commands.CreateHandoverCommand;
import com.brainspark.nursepulse.platform.handover.domain.model.queries.GetAllHandoversByPatientIdQuery;
import com.brainspark.nursepulse.platform.handover.domain.model.queries.GetHandoverByIdQuery;
import com.brainspark.nursepulse.platform.handover.domain.model.valueobjects.HandoverStatus;
import com.brainspark.nursepulse.platform.handover.domain.repositories.HandoverRepository;
import com.brainspark.nursepulse.platform.shared.application.result.ApplicationError;
import com.brainspark.nursepulse.platform.shared.application.result.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** US-13 (register SBAR), US-14 (consult handover), US-15 (acknowledge), TS-04 (SBAR API). */
class HandoverServicesTest {

    private HandoverRepository repository;
    private HandoverCommandServicesImpl commands;
    private HandoverQueryServiceImpl queries;

    @BeforeEach
    void setUp() {
        repository = mock(HandoverRepository.class);
        commands = new HandoverCommandServicesImpl(repository);
        queries = new HandoverQueryServiceImpl(repository);
    }

    private CreateHandoverCommand sbar() {
        return new CreateHandoverCommand(5L, "Traspaso turno noche", "Paciente estable", "Antecedente de IAM",
                "Sin dolor torácico", "Control de signos cada 2 h", "nurse.maria", 2L);
    }

    @Test
    void createsHandoverWithSbarStructureAndPendingStatus() {
        when(repository.save(any(Handover.class))).thenAnswer(call -> {
            Handover saved = call.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        var result = commands.handle(sbar());

        assertEquals(10L, assertInstanceOf(Result.Success.class, result).value());
        var captor = org.mockito.ArgumentCaptor.forClass(Handover.class);
        verify(repository).save(captor.capture());
        assertEquals(HandoverStatus.PENDING, captor.getValue().getStatus());
        assertEquals("S: Paciente estable | B: Antecedente de IAM | A: Sin dolor torácico | R: Control de signos cada 2 h",
                captor.getValue().getDescription());
    }

    @Test
    void commandRejectsEachMissingSbarField() {
        assertThrows(IllegalArgumentException.class, () -> new CreateHandoverCommand(5L, "t", " ", "b", "a", "r", "u", 2L));
        assertThrows(IllegalArgumentException.class, () -> new CreateHandoverCommand(5L, "t", "s", "", "a", "r", "u", 2L));
        assertThrows(IllegalArgumentException.class, () -> new CreateHandoverCommand(5L, "t", "s", "b", null, "r", "u", 2L));
        assertThrows(IllegalArgumentException.class, () -> new CreateHandoverCommand(5L, "t", "s", "b", "a", " ", "u", 2L));
        assertThrows(IllegalArgumentException.class, () -> new CreateHandoverCommand(null, "t", "s", "b", "a", "r", "u", 2L));
    }

    @Test
    void persistenceFailureBecomesUnexpectedError() {
        when(repository.save(any())).thenThrow(new RuntimeException("db down"));

        var error = (ApplicationError) assertInstanceOf(Result.Failure.class, commands.handle(sbar())).error();

        assertEquals("UNEXPECTED_ERROR", error.code());
    }

    @Test
    void acknowledgeMarksHandoverAcknowledgedWithIncomingNurse() {
        var handover = new Handover(sbar());
        handover.setCreatedAt(new java.util.Date());
        when(repository.findById(10L)).thenReturn(Optional.of(handover));
        when(repository.save(any(Handover.class))).thenAnswer(call -> call.getArgument(0));

        var result = commands.handle(new AcknowledgeHandoverCommand(10L, 2L, "Recibido sin novedades"));

        var acknowledged = (Handover) assertInstanceOf(Result.Success.class, result).value();
        assertEquals(HandoverStatus.ACKNOWLEDGED, acknowledged.getStatus());
        assertEquals(2L, acknowledged.getIncomingNurseId());
        assertEquals("Recibido sin novedades", acknowledged.getAdditionalNotes());
    }

    @Test
    void acknowledgeReturnsNotFoundForUnknownHandover() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        var error = (ApplicationError) assertInstanceOf(Result.Failure.class,
                commands.handle(new AcknowledgeHandoverCommand(99L, 2L, null))).error();

        assertEquals("HANDOVER_NOT_FOUND", error.code());
    }

    @Test
    void queriesReturnDetailAndPatientHandovers() {
        var handover = new Handover(sbar());
        when(repository.findById(10L)).thenReturn(Optional.of(handover));
        when(repository.findByPatientId(5L)).thenReturn(List.of(handover));

        assertTrue(queries.handle(new GetHandoverByIdQuery(10L)).isPresent());
        assertEquals(1, queries.handle(new GetAllHandoversByPatientIdQuery(5L, null, null)).size());
        verify(repository, never()).findByPatientIdAndDateRange(any(), any(), any());
    }

    @Test
    void queryByPatientUsesDateRangeWhenBothDatesAreGiven() {
        var from = new java.util.Date(0);
        var to = new java.util.Date();
        when(repository.findByPatientIdAndDateRange(5L, from, to)).thenReturn(List.of());

        queries.handle(new GetAllHandoversByPatientIdQuery(5L, from, to));

        verify(repository).findByPatientIdAndDateRange(5L, from, to);
    }
}
