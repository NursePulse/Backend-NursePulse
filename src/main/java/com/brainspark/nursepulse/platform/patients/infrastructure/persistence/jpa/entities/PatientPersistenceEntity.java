package com.brainspark.nursepulse.platform.patients.infrastructure.persistence.jpa.entities;

import com.brainspark.nursepulse.platform.patients.domain.model.valueobjects.PatientStatus;
import com.brainspark.nursepulse.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
public class PatientPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(nullable = false, length = 80)
    private String firstName;

    @Column(nullable = false, length = 80)
    private String lastName;

    @Column(nullable = false, unique = true, length = 20)
    private String documentNumber;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false, length = 30)
    private String gender;

    @Column(nullable = false, length = 180)
    private String diagnosis;

    @Column(nullable = false, length = 20)
    private String roomNumber;

    @Column(nullable = false, length = 20)
    private String bedNumber;

    @Column(nullable = false, length = 120)
    private String attendingPhysician;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PatientStatus status;

    @Column(nullable = false)
    private LocalDate admissionDate;
}
