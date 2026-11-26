package com.his.medcenter.clinical.application.dto;

import com.his.medcenter.clinical.domain.model.PrescriptionItem;
import com.his.medcenter.clinical.domain.model.PrescriptionItemStatus;

import java.time.Instant;
import java.util.UUID;

public record PrescriptionItemResponseDto(
        UUID id,
        UUID medicationId,
        String medicationName,
        String dosage,
        String frequency,
        String route,
        int durationDays,
        int quantity,
        String instructions,
        PrescriptionItemStatus status,
        String cancellationReason,
        Instant cancelledAt,
        Instant createdAt,
        Instant updatedAt
) {
    public static PrescriptionItemResponseDto fromDomain(PrescriptionItem item) {
        return new PrescriptionItemResponseDto(
                item.getId().value(),
                item.getMedicationId(),
                item.getMedicationName(),
                item.getDosage(),
                item.getFrequency(),
                item.getRoute(),
                item.getDurationDays(),
                item.getQuantity(),
                item.getInstructions(),
                item.getStatus(),
                item.getCancellationReason(),
                item.getCancelledAt(),
                item.getCreatedAt(),
                item.getUpdatedAt()
        );
    }
}