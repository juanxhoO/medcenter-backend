package com.his.medcenter.patient.application.dto;

import com.his.medcenter.patient.domain.model.BloodType;
import com.his.medcenter.patient.domain.model.Gender;
import com.his.medcenter.patient.domain.model.MaritalStatus;
import com.his.medcenter.patient.domain.model.Patient;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record PatientResponseDto(
        UUID id,
        UUID userId,
        String medicalRecordNumber,
        LocalDate dateOfBirth,
        int age,
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
        String chronicConditions,
        Instant createdAt,
        Instant updatedAt
) {
    public static PatientResponseDto fromDomain(Patient patient) {
        return new PatientResponseDto(
                patient.getId().value(),
                patient.getUserId(),
                patient.getMedicalRecordNumber(),
                patient.getDateOfBirth(),
                patient.getAge(),
                patient.getGender(),
                patient.getNationalId(),
                patient.getAddress(),
                patient.getBloodType(),
                patient.getMaritalStatus(),
                patient.getOccupation(),
                patient.getEmergencyContactName(),
                patient.getEmergencyContactPhone(),
                patient.getEmergencyContactRelationship(),
                patient.getInsuranceProvider(),
                patient.getInsurancePolicyNumber(),
                patient.getAllergies(),
                patient.getChronicConditions(),
                patient.getCreatedAt(),
                patient.getUpdatedAt()
        );
    }
}

