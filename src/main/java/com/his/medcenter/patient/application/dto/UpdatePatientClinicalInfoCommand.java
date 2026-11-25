package com.his.medcenter.patient.application.dto;

import com.his.medcenter.patient.domain.model.BloodType;

public record UpdatePatientClinicalInfoCommand(
        BloodType bloodType,
        String allergies,
        String chronicConditions
) {
}

