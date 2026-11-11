package com.his.medcenter.clinical.application.dto;

import java.math.BigDecimal;

public record UpdateVitalSignsCommand(
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