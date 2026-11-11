package com.his.medcenter.clinical.infrastructure.rest.dto;

import com.his.medcenter.clinical.application.dto.UpdateVitalSignsCommand;

import java.math.BigDecimal;

public record UpdateVitalSignsRequest(
        BigDecimal temperature,
        Integer systolicBloodPressure,
        Integer diastolicBloodPressure,
        Integer heartRate,
        Integer respiratoryRate,
        BigDecimal oxygenSaturation,
        BigDecimal height,
        BigDecimal weight
) {
    public UpdateVitalSignsCommand toCommand() {
        return new UpdateVitalSignsCommand(
                temperature,
                systolicBloodPressure,
                diastolicBloodPressure,
                heartRate,
                respiratoryRate,
                oxygenSaturation,
                height,
                weight
        );
    }
}