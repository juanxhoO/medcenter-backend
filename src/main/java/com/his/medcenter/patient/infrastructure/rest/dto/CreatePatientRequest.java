package com.his.medcenter.patient.infrastructure.rest.dto;

import com.his.medcenter.patient.application.dto.CreatePatientCommand;
import com.his.medcenter.patient.domain.model.BloodType;
import com.his.medcenter.patient.domain.model.Gender;
import com.his.medcenter.patient.domain.model.MaritalStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;
import java.util.UUID;

public record CreatePatientRequest(

        @NotNull(message = "User ID is required")
        UUID userId,

        @NotBlank(message = "Medical record number is required")
        String medicalRecordNumber,

        @NotNull(message = "Date of birth is required")
        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,

        @NotNull(message = "Gender is required")
        Gender gender,

        String nationalId,

        String address,

        BloodType bloodType,

        MaritalStatus maritalStatus,

        String occupation,

        String emergencyContactName,

        String emergencyContactPhone,

        String emergencyContactRelationship,

        String insuranceProvider,

        String insurancePolicyNumber,

        String allergies,

        String chronicConditions
) {
    public CreatePatientCommand toCommand() {
        return new CreatePatientCommand(
                userId,
                medicalRecordNumber,
                dateOfBirth,
                gender,
                nationalId,
                address,
                bloodType,
                maritalStatus,
                occupation,
                emergencyContactName,
                emergencyContactPhone,
                emergencyContactRelationship,
                insuranceProvider,
                insurancePolicyNumber,
                allergies,
                chronicConditions
        );
    }
}

