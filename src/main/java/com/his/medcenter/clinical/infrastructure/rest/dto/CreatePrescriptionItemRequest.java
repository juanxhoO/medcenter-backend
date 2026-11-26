package com.his.medcenter.clinical.infrastructure.rest.dto;

import com.his.medcenter.clinical.application.dto.CreatePrescriptionItemCommand;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CreatePrescriptionItemRequest(

        UUID medicationId,

        @NotBlank(message = "Medication name is required")
        String medicationName,

        @NotBlank(message = "Dosage is required")
        String dosage,

        @NotBlank(message = "Frequency is required")
        String frequency,

        @NotBlank(message = "Route is required")
        String route,

        @Min(value = 1, message = "Duration must be at least 1 day")
        int durationDays,

        @Min(value = 1, message = "Quantity must be at least 1")
        int quantity,

        String instructions

) {
    public CreatePrescriptionItemCommand toCommand() {
        return new CreatePrescriptionItemCommand(
                medicationId,
                medicationName,
                dosage,
                frequency,
                route,
                durationDays,
                quantity,
                instructions
        );
    }
}