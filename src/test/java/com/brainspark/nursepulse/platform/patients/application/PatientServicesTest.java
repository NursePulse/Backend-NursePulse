package com.brainspark.nursepulse.platform.patients.application;

import com.brainspark.nursepulse.platform.patients.application.internal.commandservices.PatientCommandServiceImpl;
import com.brainspark.nursepulse.platform.patients.application.internal.queryservices.PatientQueryServiceImpl;
import com.brainspark.nursepulse.platform.patients.domain.model.aggregates.Patient;
import com.brainspark.nursepulse.platform.patients.domain.model.commands.CreatePatientCommand;
import com.brainspark.nursepulse.platform.patients.domain.model.commands.DeletePatientCommand;
import com.brainspark.nursepulse.platform.patients.domain.model.commands.UpdatePatientCommand;
import com.brainspark.nursepulse.platform.patients.domain.model.queries.GetAllPatientsQuery;
import com.brainspark.nursepulse.platform.patients.domain.model.queries.GetPatientByIdQuery;
import com.brainspark.nursepulse.platform.patients.domain.model.valueobjects.PatientStatus;
import com.brainspark.nursepulse.platform.patients.domain.repositories.PatientRepository;
import com.brainspark.nursepulse.platform.shared.application.result.ApplicationError;
import com.brainspark.nursepulse.platform.shared.application.result.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** US-27 (register/edit patients), US-28 (list and detail), TS-02 (patients API). */
class PatientServicesTest {

    private PatientRepository repository;
    private PatientCommandServiceImpl commands;
    private PatientQueryServiceImpl queries;

    @BeforeEach
    void setUp() {
        repository = mock(PatientRepository.class);
        commands = new PatientCommandServiceImpl(repository);
        queries = new PatientQueryServiceImpl(repository);
        when(repository.save(any(Patient.class))).thenAnswer(call -> call.getArgument(0));
    }

    private CreatePatientCommand create(String document, LocalDate birthDate) {
        return new CreatePatientCommand("  María ", "Núñez", document, birthDate, "F",
                "Insuficiencia cardiaca", "101", "A", "Dr. Torres", null, null);
    }

    private UpdatePatientCommand update(Long id, String document) {
        return new UpdatePatientCommand(id, "María", "Núñez", document, LocalDate.of(1980, 5, 1), "F",
                "Arritmia", "102", "B", "Dr. Torres", PatientStatus.STABLE, LocalDate.of(2026, 1, 1));
    }

    @Test
    void registersPatientTrimmingFieldsAndDefaultingStatusToObservation() {
        when(repository.existsByDocumentNumber("12345678")).thenReturn(false);

        var result = commands.handle(create("12345678", LocalDate.of(1980, 5, 1)));

        var patient = assertInstanceOf(Result.Success.class, result).value();
        var saved = assertInstanceOf(Patient.class, patient);
        assertEquals("María", saved.getFirstName());
        assertEquals(PatientStatus.OBSERVATION, saved.getStatus());
        assertEquals(LocalDate.now(), saved.getAdmissionDate());
        verify(repository).save(any(Patient.class));
    }

    @Test
    void rejectsDuplicateDocumentNumberAsConflict() {
        when(repository.existsByDocumentNumber("12345678")).thenReturn(true);

        var result = commands.handle(create("12345678", LocalDate.of(1980, 5, 1)));

        var error = assertInstanceOf(Result.Failure.class, result).error();
        assertEquals("PATIENT_CONFLICT", ((ApplicationError) error).code());
        verify(repository, never()).save(any());
    }

    @Test
    void rejectsBirthDateBefore1930AndFutureBirthDate() {
        var tooOld = commands.handle(create("1", LocalDate.of(1929, 12, 31)));
        var future = commands.handle(create("2", LocalDate.now().plusDays(1)));

        assertTrue(tooOld.isFailure());
        assertTrue(future.isFailure());
        verify(repository, never()).save(any());
    }

    @Test
    void rejectsMissingRequiredField() {
        var command = new CreatePatientCommand(" ", "Núñez", "3", LocalDate.of(1980, 5, 1), "F",
                "Dx", "1", "A", "Dr", null, null);

        var result = commands.handle(command);

        var error = (ApplicationError) assertInstanceOf(Result.Failure.class, result).error();
        assertEquals("BUSINESS_RULE_VIOLATION", error.code());
        assertTrue(error.details().contains("First name is required"));
    }

    @Test
    void updatesAnExistingPatient() {
        var existing = new Patient(create("12345678", LocalDate.of(1980, 5, 1)));
        when(repository.findById(7L)).thenReturn(Optional.of(existing));

        var result = commands.handle(update(7L, "12345678"));

        var saved = (Patient) assertInstanceOf(Result.Success.class, result).value();
        assertEquals("Arritmia", saved.getDiagnosis());
        assertEquals(PatientStatus.STABLE, saved.getStatus());
    }

    @Test
    void updateReturnsNotFoundForUnknownPatient() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        var error = (ApplicationError) assertInstanceOf(Result.Failure.class,
                commands.handle(update(99L, "1"))).error();

        assertEquals("PATIENT_NOT_FOUND", error.code());
    }

    @Test
    void updateRejectsChangingToADocumentNumberOfAnotherPatient() {
        var existing = new Patient(create("12345678", LocalDate.of(1980, 5, 1)));
        when(repository.findById(7L)).thenReturn(Optional.of(existing));
        when(repository.existsByDocumentNumber("87654321")).thenReturn(true);

        var error = (ApplicationError) assertInstanceOf(Result.Failure.class,
                commands.handle(update(7L, "87654321"))).error();

        assertEquals("PATIENT_CONFLICT", error.code());
    }

    @Test
    void deletesExistingPatientAndReportsNotFoundOtherwise() {
        when(repository.findById(7L)).thenReturn(Optional.of(new Patient()));
        when(repository.findById(8L)).thenReturn(Optional.empty());

        assertTrue(commands.handle(new DeletePatientCommand(7L)).isSuccess());
        verify(repository).deleteById(7L);

        var error = (ApplicationError) assertInstanceOf(Result.Failure.class,
                commands.handle(new DeletePatientCommand(8L))).error();
        assertEquals("PATIENT_NOT_FOUND", error.code());
    }

    @Test
    void queryReturnsPatientDetailOr404() {
        var patient = new Patient(create("12345678", LocalDate.of(1980, 5, 1)));
        when(repository.findById(1L)).thenReturn(Optional.of(patient));
        when(repository.findById(2L)).thenReturn(Optional.empty());

        assertTrue(queries.handle(new GetPatientByIdQuery(1L)).isSuccess());
        var error = (ApplicationError) assertInstanceOf(Result.Failure.class,
                queries.handle(new GetPatientByIdQuery(2L))).error();
        assertEquals("PATIENT_NOT_FOUND", error.code());
    }

    @Test
    void queryListsAllPatients() {
        var patient = new Patient(create("12345678", LocalDate.of(1980, 5, 1)));
        when(repository.findAll()).thenReturn(List.of(patient));

        var list = assertInstanceOf(Result.Success.class, queries.handle(new GetAllPatientsQuery())).value();

        assertEquals(1, ((List<?>) list).size());
    }
}
