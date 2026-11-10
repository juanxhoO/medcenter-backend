package com.his.medcenter.clinical.infrastructure.rest.dto;

import com.his.medcenter.clinical.application.dto.UpdateEncounterCommand;
import com.his.medcenter.clinical.domain.model.EncounterPriority;

public record UpdateEncounterRequest(
        String reason,
        String notes,
        EncounterPriority priority
) {
    public UpdateEncounterCommand toCommand() {
        return new UpdateEncounterCommand(
                reason,
                notes,
                priority
        );
    }
}
