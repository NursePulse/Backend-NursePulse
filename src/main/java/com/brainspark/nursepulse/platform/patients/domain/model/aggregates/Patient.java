package com.brainspark.nursepulse.platform.patients.domain.model.aggregates;

import com.brainspark.nursepulse.platform.patients.domain.exceptions.InvalidPatientException;
import com.brainspark.nursepulse.platform.patients.domain.model.commands.CreatePatientCommand;
import com.brainspark.nursepulse.platform.patients.domain.model.commands.UpdatePatientCommand;
import com.brainspark.nursepulse.platform.patients.domain.model.valueobjects.PatientStatus;
import com.brainspark.nursepulse.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
public class Patient extends AbstractDomainAggregateRoot<Patient> {

    @Setter
    private Long id;

    private String firstName;
    private String lastName;
    private String documentNumber;
    private LocalDate birthDate;
    private String gender;
    private String diagnosis;
    private String roomNumber;
    private String bedNumber;
    private String attendingPhysician;
    private PatientStatus status;
    private LocalDate admissionDate;

    public Patient() {
    }

    public Patient(CreatePatientCommand command) {
        validate(command);

        this.firstName = command.firstName().trim();
        this.lastName = command.lastName().trim();
        this.documentNumber = command.documentNumber().trim();
        this.birthDate = command.birthDate();
        this.gender = command.gender().trim();
        this.diagnosis = command.diagnosis().trim();
        this.roomNumber = command.roomNumber().trim();
        this.bedNumber = command.bedNumber().trim();
        this.attendingPhysician = command.attendingPhysician().trim();
        this.status = command.status() != null ? command.status() : PatientStatus.OBSERVATION;
        this.admissionDate = command.admissionDate() != null ? command.admissionDate() : LocalDate.now();
    }

    private Patient(
            Long id,
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
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.documentNumber = documentNumber;
        this.birthDate = birthDate;
        this.gender = gender;
        this.diagnosis = diagnosis;
        this.roomNumber = roomNumber;
        this.bedNumber = bedNumber;
        this.attendingPhysician = attendingPhysician;
        this.status = status;
        this.admissionDate = admissionDate;
    }

    public void update(UpdatePatientCommand command) {
        validate(command);

        this.firstName = command.firstName().trim();
        this.lastName = command.lastName().trim();
        this.documentNumber = command.documentNumber().trim();
        this.birthDate = command.birthDate();
        this.gender = command.gender().trim();
        this.diagnosis = command.diagnosis().trim();
        this.roomNumber = command.roomNumber().trim();
        this.bedNumber = command.bedNumber().trim();
        this.attendingPhysician = command.attendingPhysician().trim();
        this.status = command.status() != null ? command.status() : PatientStatus.OBSERVATION;
        this.admissionDate = command.admissionDate() != null ? command.admissionDate() : LocalDate.now();
    }

    private void validate(CreatePatientCommand command) {
        validatePatientData(
                command.firstName(),
                command.lastName(),
                command.documentNumber(),
                command.birthDate(),
                command.gender(),
                command.diagnosis(),
                command.roomNumber(),
                command.bedNumber(),
                command.attendingPhysician()
        );
    }

    private void validate(UpdatePatientCommand command) {
        if (command.patientId() == null || command.patientId() <= 0) {
            throw new InvalidPatientException("Patient id is required");
        }

        validatePatientData(
                command.firstName(),
                command.lastName(),
                command.documentNumber(),
                command.birthDate(),
                command.gender(),
                command.diagnosis(),
                command.roomNumber(),
                command.bedNumber(),
                command.attendingPhysician()
        );
    }

    private void validatePatientData(
            String firstName,
            String lastName,
            String documentNumber,
            LocalDate birthDate,
            String gender,
            String diagnosis,
            String roomNumber,
            String bedNumber,
            String attendingPhysician
    ) {
        if (firstName == null || firstName.isBlank()) {
            throw new InvalidPatientException("First name is required");
        }

        if (lastName == null || lastName.isBlank()) {
            throw new InvalidPatientException("Last name is required");
        }

        if (documentNumber == null || documentNumber.isBlank()) {
            throw new InvalidPatientException("Document number is required");
        }

        if (birthDate == null) {
            throw new InvalidPatientException("Birth date is required");
        }

        if (birthDate.isAfter(LocalDate.now())) {
            throw new InvalidPatientException("Birth date cannot be in the future");
        }

        if (gender == null || gender.isBlank()) {
            throw new InvalidPatientException("Gender is required");
        }

        if (diagnosis == null || diagnosis.isBlank()) {
            throw new InvalidPatientException("Diagnosis is required");
        }

        if (roomNumber == null || roomNumber.isBlank()) {
            throw new InvalidPatientException("Room number is required");
        }

        if (bedNumber == null || bedNumber.isBlank()) {
            throw new InvalidPatientException("Bed number is required");
        }

        if (attendingPhysician == null || attendingPhysician.isBlank()) {
            throw new InvalidPatientException("Attending physician is required");
        }
    }

    public String getFullName() {
        return "%s %s".formatted(firstName, lastName);
    }

    public static Patient reconstitute(
            Long id,
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
        return new Patient(
                id,
                firstName,
                lastName,
                documentNumber,
                birthDate,
                gender,
                diagnosis,
                roomNumber,
                bedNumber,
                attendingPhysician,
                status,
                admissionDate
        );
    }
}