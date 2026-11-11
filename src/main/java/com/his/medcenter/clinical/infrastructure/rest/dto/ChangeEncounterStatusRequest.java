package com.his.medcenter.clinical.infrastructure.rest.dto;

import com.his.medcenter.clinical.domain.model.EncounterStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeEncounterStatusRequest(
        @NotNull(message = "Encounter status is required")
        EncounterStatus status,

        String diagnosis,

        String reason,

        String notes
) {
}

