package com.his.medcenter.patient.application.dto;

public record UpdateInsuranceCommand(
        String insuranceProvider,
        String insurancePolicyNumber
) {
}

