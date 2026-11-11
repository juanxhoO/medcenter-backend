package com.his.medcenter.clinical.application.dto;

import com.his.medcenter.clinical.domain.model.EncounterPriority;

public record UpdateEncounterCommand(
        String reason,
        String notes,
        EncounterPriority priority
) {
}
