package com.his.medcenter.clinical.application.dto;

import com.his.medcenter.clinical.domain.model.EncounterPriority;

public record UpdateEncounterCommand(
        String reason,
        String diagnosis,
        String notes,
        EncounterPriority priority
) {
}

