package com.brainspark.nursepulse.platform.vitalsigns.interfaces.rest.resources;

public record VitalSignRecordResource(Long id, Long patientId, Long nurseId, Integer heartRate, Integer respiratoryRate,
                                      Integer systolic, Integer diastolic, Integer oxygenSaturation,
                                      java.math.BigDecimal temperature,
                                      com.brainspark.nursepulse.platform.vitalsigns.domain.model.valueobjects.RiskLevel riskLevel,
                                      java.time.LocalDateTime recordedAt) {
}
