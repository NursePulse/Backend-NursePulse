package com.brainspark.nursepulse.platform.patients.domain.model.commands;

import com.brainspark.nursepulse.platform.patients.domain.model.valueobjects.PatientStatus;

import java.time.LocalDate;

public record UpdatePatientCommand(
        Long patientId,
        String firstName,
        String lastName,
        String documentNumber,
        LocalDate birthDate,
        String gender,
        String diagnosis,
        String roomNumber,
        String bedNumber,
        String attendingPhysician,
        PatientStatus status,
        LocalDate admissionDate
) {
}