package com.his.medcenter.clinical.application.dto;

import java.util.UUID;

public record CreatePrescriptionItemCommand(
        UUID medicationId,
        String medicationName,
        String dosage,
        String frequency,
        String route,
        int durationDays,
        int quantity,
        String instructions
) {
}