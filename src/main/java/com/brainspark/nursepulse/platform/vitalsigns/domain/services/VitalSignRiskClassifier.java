package com.brainspark.nursepulse.platform.vitalsigns.domain.services;

import com.brainspark.nursepulse.platform.vitalsigns.domain.model.valueobjects.BloodPressure;
import com.brainspark.nursepulse.platform.vitalsigns.domain.model.valueobjects.RiskLevel;

import java.math.BigDecimal;

/**
 * Classifies a set of vital sign readings into an overall {@link RiskLevel}.
 *
 * The result is the most severe level found across all readings (US-24): a single
 * critical finding (e.g. SpO2 &lt; 90%) makes the whole record CRITICAL even if the
 * other readings are normal.
 */
public final class VitalSignRiskClassifier {

    private VitalSignRiskClassifier() {}

    public static RiskLevel classify(
            Integer heartRate,
            Integer respiratoryRate,
            BloodPressure bloodPressure,
            Integer oxygenSaturation,
            BigDecimal temperature
    ) {
        RiskLevel level = RiskLevel.LOW;
        level = escalate(level, oxygenSaturationRisk(oxygenSaturation));
        level = escalate(level, bloodPressureRisk(bloodPressure));
        level = escalate(level, heartRateRisk(heartRate));
        level = escalate(level, respiratoryRateRisk(respiratoryRate));
        level = escalate(level, temperatureRisk(temperature));
        return level;
    }

    private static RiskLevel oxygenSaturationRisk(Integer value) {
        if (value == null) return RiskLevel.LOW;
        if (value < 90) return RiskLevel.CRITICAL;
        if (value < 94) return RiskLevel.HIGH;
        if (value < 95) return RiskLevel.MEDIUM;
        return RiskLevel.LOW;
    }

    private static RiskLevel bloodPressureRisk(BloodPressure value) {
        if (value == null) return RiskLevel.LOW;
        int systolic = value.systolic();
        int diastolic = value.diastolic();

        if (systolic >= 160 || diastolic >= 100) return RiskLevel.CRITICAL;
        if (systolic >= 145 || diastolic >= 95) return RiskLevel.HIGH;
        if (systolic >= 130 || diastolic >= 85) return RiskLevel.MEDIUM;
        return RiskLevel.LOW;
    }

    private static RiskLevel heartRateRisk(Integer value) {
        if (value == null) return RiskLevel.LOW;
        if (value >= 130 || value < 40) return RiskLevel.CRITICAL;
        if (value >= 110 || value < 50) return RiskLevel.HIGH;
        if (value > 100 || value < 60) return RiskLevel.MEDIUM;
        return RiskLevel.LOW;
    }

    private static RiskLevel respiratoryRateRisk(Integer value) {
        if (value == null) return RiskLevel.LOW;
        if (value >= 30 || value < 8) return RiskLevel.CRITICAL;
        if (value >= 24 || value < 10) return RiskLevel.HIGH;
        if (value > 20 || value < 12) return RiskLevel.MEDIUM;
        return RiskLevel.LOW;
    }

    private static RiskLevel temperatureRisk(BigDecimal value) {
        if (value == null) return RiskLevel.LOW;
        double temp = value.doubleValue();

        if (temp >= 39 || temp < 35) return RiskLevel.CRITICAL;
        if (temp >= 38 || temp < 36) return RiskLevel.HIGH;
        if (temp > 37.5) return RiskLevel.MEDIUM;
        return RiskLevel.LOW;
    }

    private static RiskLevel escalate(RiskLevel current, RiskLevel candidate) {
        return candidate.ordinal() > current.ordinal() ? candidate : current;
    }
}
