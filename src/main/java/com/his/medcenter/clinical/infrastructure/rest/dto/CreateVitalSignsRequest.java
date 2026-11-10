package com.his.medcenter.clinical.infrastructure.rest.dto;

import com.his.medcenter.clinical.application.dto.CreateVitalSignsCommand;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateVitalSignsRequest(

        @NotNull(message = "Patient ID is required")
        UUID patientId,

        @NotNull(message = "Practitioner ID is required")
        UUID practitionerId,

        UUID encounterId,

        @DecimalMin(value = "0.1", message = "Temperature must be greater than 0")
        BigDecimal temperature,

        @Positive(message = "Systolic blood pressure must be positive")
        Integer systolicBloodPressure,

        @Positive(message = "Diastolic blood pressure must be positive")
        Integer diastolicBloodPressure,

        @Positive(message = "Heart rate must be positive")
        Integer heartRate,

        @Positive(message = "Respiratory rate must be positive")
        Integer respiratoryRate,

        @DecimalMin(value = "0.0", message = "Oxygen saturation cannot be below 0")
        @DecimalMax(value = "100.0", message = "Oxygen saturation cannot exceed 100")
        BigDecimal oxygenSaturation,

        @DecimalMin(value = "0.1", message = "Height must be greater than 0")
        BigDecimal height,

        @DecimalMin(value = "0.1", message = "Weight must be greater than 0")
        BigDecimal weight

) {
    public CreateVitalSignsCommand toCommand() {
        return new CreateVitalSignsCommand(
                patientId,
                practitionerId,
                encounterId,
                temperature,
                systolicBloodPressure,
                diastolicBloodPressure,
                heartRate,
                respiratoryRate,
                oxygenSaturation,
                height,
                weight
        );
    }
}