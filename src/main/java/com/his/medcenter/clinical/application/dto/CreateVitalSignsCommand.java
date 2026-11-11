package com.his.medcenter.clinical.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateVitalSignsCommand(
        UUID patientId,
        UUID practitionerId,
        UUID encounterId,
        BigDecimal temperature,
        Integer systolicBloodPressure,
        Integer diastolicBloodPressure,
        Integer heartRate,
        Integer respiratoryRate,
        BigDecimal oxygenSaturation,
        BigDecimal height,
        BigDecimal weight
) {
}