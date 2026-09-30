package com.brainspark.nursepulse.platform.patients.interfaces.rest.resources;

import com.brainspark.nursepulse.platform.patients.domain.model.valueobjects.PatientStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdatePatientResource(
        @NotBlank
        @Size(max = 80)
        @Pattern(regexp = "^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ ]+$", message = "{validation.patient.name.format}")
        String firstName,

        @NotBlank
        @Size(max = 80)
        @Pattern(regexp = "^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ ]+$", message = "{validation.patient.name.format}")
        String lastName,

        @NotBlank
        @Size(min = 8, max = 20)
        @Pattern(regexp = "^[0-9]+$", message = "{validation.document.format}")
        String documentNumber,

        @NotNull
        @PastOrPresent
        LocalDate birthDate,

        @NotBlank
        @Size(max = 30)
        String gender,

        @NotBlank
        @Size(max = 180)
        String diagnosis,

        @NotBlank
        @Size(max = 20)
        String roomNumber,

        @NotBlank
        @Size(max = 20)
        String bedNumber,

        @NotBlank
        @Size(max = 120)
        String attendingPhysician,

        PatientStatus status,

        @PastOrPresent
        LocalDate admissionDate
) {
}