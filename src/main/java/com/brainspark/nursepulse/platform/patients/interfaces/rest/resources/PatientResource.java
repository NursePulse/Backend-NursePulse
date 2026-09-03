package com.brainspark.nursepulse.platform.patients.interfaces.rest.resources;

import com.brainspark.nursepulse.platform.patients.domain.model.valueobjects.PatientStatus;

import java.time.LocalDate;

public record PatientResource(
        Long id,
        String firstName,
        String lastName,
        String fullName,
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
