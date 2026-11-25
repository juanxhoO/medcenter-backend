package com.his.medcenter.patient.application.dto;

import com.his.medcenter.patient.domain.model.Gender;
import com.his.medcenter.patient.domain.model.MaritalStatus;

import java.time.LocalDate;

public record UpdatePatientDemographicsCommand(
        LocalDate dateOfBirth,
        Gender gender,
        String nationalId,
        String address,
        MaritalStatus maritalStatus,
        String occupation
) {
}

