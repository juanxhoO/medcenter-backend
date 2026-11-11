package com.his.medcenter.clinical.application.dto;

import com.his.medcenter.clinical.domain.model.Prescription;
import com.his.medcenter.clinical.domain.model.PrescriptionStatus;

import java.time.Instant;
import java.util.UUID;

public record PrescriptionResponseDto(
        UUID id,
        UUID encounterId,
        UUID patientId,
        UUID practitionerId,
        String medicationName,
        String dosage,
        String frequency,
        String route,
        int durationDays,
        String instructions,
        PrescriptionStatus status,
        Instant prescribedAt,
        Instant expiresAt,
        Instant createdAt,
        Instant updatedAt
) {
    public static PrescriptionResponseDto fromDomain(Prescription prescription) {
        return new PrescriptionResponseDto(
                prescription.getId().value(),
                prescription.getEncounterId() != null ? prescription.getEncounterId().value() : null,
                prescription.getPatientId(),
                prescription.getPractitionerId(),
                prescription.getMedicationName(),
                prescription.getDosage(),
                prescription.getFrequency(),
                prescription.getRoute(),
                prescription.getDurationDays(),
                prescription.getInstructions(),
                prescription.getStatus(),
                prescription.getPrescribedAt(),
                prescription.getExpiresAt(),
                prescription.getCreatedAt(),
                prescription.getUpdatedAt()
        );
    }
}

