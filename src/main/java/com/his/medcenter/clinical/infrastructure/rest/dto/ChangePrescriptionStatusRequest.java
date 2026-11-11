package com.his.medcenter.clinical.infrastructure.rest.dto;

import com.his.medcenter.clinical.domain.model.PrescriptionStatus;
import jakarta.validation.constraints.NotNull;

public record ChangePrescriptionStatusRequest(
        @NotNull(message = "Prescription status is required")
        PrescriptionStatus status,

        String reason
) {
}

