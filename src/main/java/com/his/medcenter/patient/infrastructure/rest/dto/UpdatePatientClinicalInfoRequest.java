package com.his.medcenter.patient.infrastructure.rest.dto;

import com.his.medcenter.patient.application.dto.UpdatePatientClinicalInfoCommand;
import com.his.medcenter.patient.domain.model.BloodType;

public record UpdatePatientClinicalInfoRequest(
        BloodType bloodType,
        String allergies,
        String chronicConditions
) {
    public UpdatePatientClinicalInfoCommand toCommand() {
        return new UpdatePatientClinicalInfoCommand(
                bloodType,
                allergies,
                chronicConditions
        );
    }
}

