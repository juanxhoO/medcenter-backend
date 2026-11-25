package com.his.medcenter.patient.infrastructure.rest.dto;

import com.his.medcenter.patient.application.dto.UpdatePatientDemographicsCommand;
import com.his.medcenter.patient.domain.model.Gender;
import com.his.medcenter.patient.domain.model.MaritalStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record UpdatePatientDemographicsRequest(

        @NotNull(message = "Date of birth is required")
        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,

        @NotNull(message = "Gender is required")
        Gender gender,

        String nationalId,

        String address,

        MaritalStatus maritalStatus,

        String occupation
) {
    public UpdatePatientDemographicsCommand toCommand() {
        return new UpdatePatientDemographicsCommand(
                dateOfBirth,
                gender,
                nationalId,
                address,
                maritalStatus,
                occupation
        );
    }
}

