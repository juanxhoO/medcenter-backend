package com.his.medcenter.patient.application.dto;

import com.his.medcenter.patient.domain.model.BloodType;
import com.his.medcenter.patient.domain.model.Gender;
import com.his.medcenter.patient.domain.model.MaritalStatus;

import java.time.LocalDate;
import java.util.UUID;

public record CreatePatientCommand(
        UUID userId,
        String medicalRecordNumber,
        LocalDate dateOfBirth,
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
}

