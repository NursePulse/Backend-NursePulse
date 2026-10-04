package com.brainspark.nursepulse.platform.vitalsigns.application;

import com.brainspark.nursepulse.platform.shared.application.result.ApplicationError;
import com.brainspark.nursepulse.platform.shared.application.result.Result;
import com.brainspark.nursepulse.platform.vitalsigns.application.internal.commandservices.VitalSignRecordCommandServiceImpl;
import com.brainspark.nursepulse.platform.vitalsigns.application.internal.queryservices.VitalSignRecordQueryServiceImpl;
import com.brainspark.nursepulse.platform.vitalsigns.domain.exceptions.InvalidVitalSignRecordException;
import com.brainspark.nursepulse.platform.vitalsigns.domain.model.aggregates.VitalSignRecord;
import com.brainspark.nursepulse.platform.vitalsigns.domain.model.commands.CreateVitalSignRecordCommand;
import com.brainspark.nursepulse.platform.vitalsigns.domain.model.queries.GetLatestVitalSignRecordByPatientIdQuery;
import com.brainspark.nursepulse.platform.vitalsigns.domain.model.queries.GetVitalSignRecordByIdQuery;
import com.brainspark.nursepulse.platform.vitalsigns.domain.model.queries.GetVitalSignRecordsByPatientIdQuery;
import com.brainspark.nursepulse.platform.vitalsigns.domain.model.valueobjects.BloodPressure;
import com.brainspark.nursepulse.platform.vitalsigns.domain.model.valueobjects.RiskLevel;
import com.brainspark.nursepulse.platform.vitalsigns.domain.repositories.VitalSignRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** US-16 (record vital signs), US-17 (clinical evolution), TS-03 (clinical records API). */
class VitalSignServicesTest {

    private VitalSignRecordRepository repository;
    private VitalSignRecordCommandServiceImpl commands;
    private VitalSignRecordQueryServiceImpl queries;

    @BeforeEach
    void setUp() {
        repository = mock(VitalSignRecordRepository.class);
        commands = new VitalSignRecordCommandServiceImpl(repository);
        queries = new VitalSignRecordQueryServiceImpl(repository);
    }

    private CreateVitalSignRecordCommand valid() {
        return new CreateVitalSignRecordCommand(5L, 1L, 80, 16, new BloodPressure(120, 80), 98,
                new BigDecimal("36.7"), null);
    }

    @Test
    void savesValidRecordWithUnassessedRiskAndCurrentTimestamp() {
        when(repository.save(any(VitalSignRecord.class))).thenAnswer(call -> call.getArgument(0));

        var result = commands.handle(valid());

        var record = (VitalSignRecord) assertInstanceOf(Result.Success.class, result).value();
        assertEquals(5L, record.getPatientId());
        assertEquals(80, record.getHeartRate());
        assertEquals(RiskLevel.UNASSESSED, record.getRiskLevel());
        assertNotNull(record.getRecordedAt());
    }

    @Test
    void rejectsMissingOrOutOfRangeValues() {
        var bp = new BloodPressure(120, 80);
        var temp = new BigDecimal("36.7");

        assertThrows(InvalidVitalSignRecordException.class,
                () -> new CreateVitalSignRecordCommand(5L, 1L, 0, 16, bp, 98, temp, null));
        assertThrows(InvalidVitalSignRecordException.class,
                () -> new CreateVitalSignRecordCommand(5L, 1L, 80, 16, bp, 101, temp, null));
        assertThrows(InvalidVitalSignRecordException.class,
                () -> new CreateVitalSignRecordCommand(5L, 1L, 80, 16, null, 98, temp, null));
        assertThrows(InvalidVitalSignRecordException.class,
                () -> new CreateVitalSignRecordCommand(5L, 1L, 80, 16, bp, 98, BigDecimal.ZERO, null));
    }

    @Test
    void bloodPressureRequiresSystolicGreaterThanDiastolic() {
        assertThrows(InvalidVitalSignRecordException.class, () -> new BloodPressure(80, 120));
    }

    @Test
    void persistenceFailureBecomesBusinessRuleViolation() {
        when(repository.save(any())).thenThrow(new RuntimeException("db down"));

        var error = (ApplicationError) assertInstanceOf(Result.Failure.class, commands.handle(valid())).error();

        assertEquals("BUSINESS_RULE_VIOLATION", error.code());
    }

    @Test
    void returnsHistoryAndLatestRecordOfAPatient() {
        var record = new VitalSignRecord(valid());
        when(repository.findByPatientId(5L)).thenReturn(List.of(record));
        when(repository.findLatestByPatientId(5L)).thenReturn(Optional.of(record));

        var history = assertInstanceOf(Result.Success.class,
                queries.handle(new GetVitalSignRecordsByPatientIdQuery(5L))).value();
        assertEquals(1, ((List<?>) history).size());
        assertTrue(queries.handle(new GetLatestVitalSignRecordByPatientIdQuery(5L)).isSuccess());
    }

    @Test
    void returnsNotFoundWhenThereIsNoRecord() {
        when(repository.findById(9L)).thenReturn(Optional.empty());
        when(repository.findLatestByPatientId(9L)).thenReturn(Optional.empty());

        assertTrue(queries.handle(new GetVitalSignRecordByIdQuery(9L)).isFailure());
        assertTrue(queries.handle(new GetLatestVitalSignRecordByPatientIdQuery(9L)).isFailure());
    }
}
