package com.brainspark.nursepulse.platform.vitalsigns.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class BloodPressurePersistenceEmbeddable {

    @Column(name = "systolic_pressure", nullable = false)
    private Integer systolic;

    @Column(name = "diastolic_pressure", nullable = false)
    private Integer diastolic;

    public BloodPressurePersistenceEmbeddable(Integer systolic, Integer diastolic) {
        this.systolic = systolic;
        this.diastolic = diastolic;
    }
}