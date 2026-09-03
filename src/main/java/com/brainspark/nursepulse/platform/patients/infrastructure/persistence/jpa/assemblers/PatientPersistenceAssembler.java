package com.brainspark.nursepulse.platform.patients.infrastructure.persistence.jpa.assemblers;

import com.brainspark.nursepulse.platform.patients.domain.model.aggregates.Patient;
import com.brainspark.nursepulse.platform.patients.infrastructure.persistence.jpa.entities.PatientPersistenceEntity;

public final class PatientPersistenceAssembler {

    private PatientPersistenceAssembler() {
    }

    public static PatientPersistenceEntity toPersistenceFromDomain(Patient patient) {
        var entity = new PatientPersistenceEntity();

        entity.setId(patient.getId());
        entity.setFirstName(patient.getFirstName());
        entity.setLastName(patient.getLastName());
        entity.setDocumentNumber(patient.getDocumentNumber());
        entity.setBirthDate(patient.getBirthDate());
        entity.setGender(patient.getGender());
        entity.setDiagnosis(patient.getDiagnosis());
        entity.setRoomNumber(patient.getRoomNumber());
        entity.setBedNumber(patient.getBedNumber());
        entity.setAttendingPhysician(patient.getAttendingPhysician());
        entity.setStatus(patient.getStatus());
        entity.setAdmissionDate(patient.getAdmissionDate());

        return entity;
    }

    public static Patient toDomainFromPersistence(PatientPersistenceEntity entity) {
        return Patient.reconstitute(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getDocumentNumber(),
                entity.getBirthDate(),
                entity.getGender(),
                entity.getDiagnosis(),
                entity.getRoomNumber(),
                entity.getBedNumber(),
                entity.getAttendingPhysician(),
                entity.getStatus(),
                entity.getAdmissionDate()
        );
    }
}
