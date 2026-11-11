package com.his.medcenter.clinical.infrastructure.rest.dto;

import com.his.medcenter.clinical.application.dto.CompleteEncounterCommand;
import jakarta.validation.constraints.NotBlank;

public record CompleteEncounterRequest(
        @NotBlank(message = "Final diagnosis is required upon encounter completion")
        String diagnosis,

        String notes
) {
    public CompleteEncounterCommand toCommand() {
        return new CompleteEncounterCommand(
                diagnosis,
                notes
        );
    }
}

