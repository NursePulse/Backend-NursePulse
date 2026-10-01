package com.brainspark.nursepulse.platform.criticalevents.application.internal.commandservices;

import com.brainspark.nursepulse.platform.criticalevents.domain.model.aggregates.Alert;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.commands.AttendAlertCommand;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.commands.CloseAlertCommand;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.commands.CreateAlertCommand;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.valueobjects.AlertSeverity;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.valueobjects.AlertStatus;
import com.brainspark.nursepulse.platform.criticalevents.domain.model.valueobjects.AlertType;
import com.brainspark.nursepulse.platform.criticalevents.domain.repositories.AlertRepository;
import com.brainspark.nursepulse.platform.iam.domain.model.aggregates.User;
import com.brainspark.nursepulse.platform.iam.domain.model.entities.Role;
import com.brainspark.nursepulse.platform.iam.domain.model.valueobjects.Roles;
import com.brainspark.nursepulse.platform.iam.domain.repositories.UserRepository;
import com.brainspark.nursepulse.platform.shared.application.notifications.SmsNotificationService;
import com.brainspark.nursepulse.platform.shared.application.result.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AlertCommandServiceImplTest {

    private AlertRepository alertRepository;
    private SmsNotificationService smsNotificationService;
    private UserRepository userRepository;
    private AlertCommandServiceImpl service;

    @BeforeEach
    void setUp() {
        alertRepository = mock(AlertRepository.class);
        smsNotificationService = mock(SmsNotificationService.class);
        userRepository = mock(UserRepository.class);
        service = new AlertCommandServiceImpl(alertRepository, smsNotificationService, userRepository);
    }

    @Test
    void shouldSendSmsToAllDoctorsWithPhoneWhenAlertIsCritical() {
        var doctorRole = new Role(1L, Roles.ROLE_DOCTOR);
        var nurseRole = new Role(2L, Roles.ROLE_NURSE);

        var doctor1 = new User("dr.smith", "pass", "John", "Smith", "smith@example.com", "987654321", 40, List.of(doctorRole));
        var doctor2 = new User("dr.jones", "pass", "Sarah", "Jones", "jones@example.com", "912345678", 38, List.of(doctorRole));
        var doctorWithoutPhone = new User("dr.nophone", "pass", "Bob", "NoPhone", "nophone@example.com", null, 45, List.of(doctorRole));
        var nurse = new User("nurse.clara", "pass", "Clara", "Oswald", "clara@example.com", "999999999", 28, List.of(nurseRole));

        when(userRepository.findAll()).thenReturn(List.of(doctor1, doctor2, doctorWithoutPhone, nurse));
        when(alertRepository.save(any(Alert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var command = new CreateAlertCommand(
                42L,
                AlertType.CARDIAC,
                AlertSeverity.CRITICAL,
                "Paciente con taquicardia severa",
                "nurse.clara"
        );

        var result = service.handle(command);

        var success = assertInstanceOf(Result.Success.class, result);
        var savedAlert = assertInstanceOf(Alert.class, success.value());
        assertEquals(AlertSeverity.CRITICAL, savedAlert.getSeverity());

        verify(smsNotificationService).sendCriticalAlertSms(
                "987654321",
                "Alerta critica en paciente #42: Paciente con taquicardia severa"
        );
        verify(smsNotificationService).sendCriticalAlertSms(
                "912345678",
                "Alerta critica en paciente #42: Paciente con taquicardia severa"
        );
        verify(smsNotificationService, never()).sendCriticalAlertSms(eq("999999999"), any());
        verify(smsNotificationService, times(2)).sendCriticalAlertSms(anyString(), anyString());
    }

    @Test
    void shouldNotSendSmsWhenAlertIsNotCritical() {
        when(alertRepository.save(any(Alert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var command = new CreateAlertCommand(
                42L,
                AlertType.RESPIRATORY,
                AlertSeverity.HIGH,
                "Dificultad respiratoria moderada",
                "nurse.clara"
        );

        var result = service.handle(command);

        var success = assertInstanceOf(Result.Success.class, result);
        var savedAlert = assertInstanceOf(Alert.class, success.value());
        assertEquals(AlertSeverity.HIGH, savedAlert.getSeverity());

        verifyNoInteractions(userRepository);
        verifyNoInteractions(smsNotificationService);
    }

    @Test
    void shouldAttendOpenAlertSuccessfully() {
        var command = new CreateAlertCommand(
                10L,
                AlertType.FALL,
                AlertSeverity.MEDIUM,
                "Paciente cayo de la cama",
                "nurse.maria"
        );
        var openAlert = new Alert(command);
        openAlert.setId(5L);

        when(alertRepository.findById(5L)).thenReturn(Optional.of(openAlert));
        when(alertRepository.save(any(Alert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var attendCommand = new AttendAlertCommand(5L, "dr.house");
        var result = service.handle(attendCommand);

        var success = assertInstanceOf(Result.Success.class, result);
        var updatedAlert = assertInstanceOf(Alert.class, success.value());
        assertEquals(AlertStatus.ATTENDED, updatedAlert.getStatus());
        assertEquals("dr.house", updatedAlert.getAttendedBy());
    }

    @Test
    void shouldCloseAttendedAlertSuccessfully() {
        var command = new CreateAlertCommand(
                10L,
                AlertType.FALL,
                AlertSeverity.MEDIUM,
                "Paciente cayo de la cama",
                "nurse.maria"
        );
        var alert = new Alert(command);
        alert.setId(5L);
        alert.attend(new AttendAlertCommand(5L, "dr.house"));

        when(alertRepository.findById(5L)).thenReturn(Optional.of(alert));
        when(alertRepository.save(any(Alert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var closeCommand = new CloseAlertCommand(5L, "dr.house", "Paciente estable y reubicado");
        var result = service.handle(closeCommand);

        var success = assertInstanceOf(Result.Success.class, result);
        var updatedAlert = assertInstanceOf(Alert.class, success.value());
        assertEquals(AlertStatus.CLOSED, updatedAlert.getStatus());
        assertEquals("dr.house", updatedAlert.getClosedBy());
        assertEquals("Paciente estable y reubicado", updatedAlert.getResolutionNotes());
    }
}
