package com.his.medcenter.clinical.application.dto;

import java.util.UUID;

public record CreatePrescriptionCommand(
        UUID encounterId,
        UUID patientId,
        UUID practitionerId,
        String medicationName,
        String dosage,
        String frequency,
        String route,
        int durationDays,
        String instructions
) {
}

