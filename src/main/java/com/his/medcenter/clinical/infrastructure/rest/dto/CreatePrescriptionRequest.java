package com.his.medcenter.clinical.infrastructure.rest.dto;

import com.his.medcenter.clinical.application.dto.CreatePrescriptionCommand;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreatePrescriptionRequest(
        UUID encounterId,

        @NotNull(message = "Patient ID is required")
        UUID patientId,

        @NotNull(message = "Practitioner ID is required")
        UUID practitionerId,

        @NotBlank(message = "Medication name is required")
        String medicationName,

        @NotBlank(message = "Dosage is required")
        String dosage,

        @NotBlank(message = "Frequency is required")
        String frequency,

        String route,

        @NotNull(message = "Duration in days is required")
        @Min(value = 1, message = "Duration must be at least 1 day")
        Integer durationDays,

        String instructions
) {
    public CreatePrescriptionCommand toCommand() {
        return new CreatePrescriptionCommand(
                encounterId,
                patientId,
                practitionerId,
                medicationName,
                dosage,
                frequency,
                route,
                durationDays != null ? durationDays : 1,
                instructions
        );
    }
}

