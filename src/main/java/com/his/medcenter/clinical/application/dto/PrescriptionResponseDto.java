package com.his.medcenter.clinical.application.dto;

import com.his.medcenter.clinical.domain.model.Prescription;
import com.his.medcenter.clinical.domain.model.PrescriptionStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PrescriptionResponseDto(
        UUID id,
        UUID diagnoseId,
        UUID encounterId,
        UUID patientId,
        UUID practitionerId,
        PrescriptionStatus status,
        Instant prescribedAt,
        Instant expiresAt,
        Instant createdAt,
        Instant updatedAt,
        List<PrescriptionItemResponseDto> items
) {
    public static PrescriptionResponseDto fromDomain(Prescription prescription) {
        return new PrescriptionResponseDto(
                prescription.getId().value(),
                prescription.getDiagnoseId() != null
                        ? prescription.getDiagnoseId().value()
                        : null,
                prescription.getEncounterId() != null
                        ? prescription.getEncounterId().value()
                        : null,
                prescription.getPatientId(),
                prescription.getPractitionerId(),
                prescription.getStatus(),
                prescription.getPrescribedAt(),
                prescription.getExpiresAt(),
                prescription.getCreatedAt(),
                prescription.getUpdatedAt(),
                prescription.getItems().stream()
                        .map(PrescriptionItemResponseDto::fromDomain)
                        .toList()
        );
    }
}