package com.his.medcenter.patient.infrastructure.rest.dto;

import com.his.medcenter.patient.application.dto.UpdateInsuranceCommand;

public record UpdateInsuranceRequest(
        String insuranceProvider,
        String insurancePolicyNumber
) {
    public UpdateInsuranceCommand toCommand() {
        return new UpdateInsuranceCommand(
                insuranceProvider,
                insurancePolicyNumber
        );
    }
}

