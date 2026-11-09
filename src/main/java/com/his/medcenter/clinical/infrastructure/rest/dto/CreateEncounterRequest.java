package com.his.medcenter.clinical.infrastructure.rest.dto;

import com.his.medcenter.clinical.application.dto.CreateEncounterCommand;
import com.his.medcenter.clinical.domain.model.EncounterPriority;
import com.his.medcenter.clinical.domain.model.EncounterType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateEncounterRequest(
        @NotNull(message = "Patient ID is required")
        UUID patientId,

        @NotNull(message = "Practitioner ID is required")
        UUID practitionerId,

        @NotNull(message = "Encounter type is required")
        EncounterType type,

        @NotNull(message = "Priority is required")
        EncounterPriority priority,

        @NotBlank(message = "Chief complaint / reason is required")
        String reason,

        String notes,

        boolean startImmediately
) {
    public CreateEncounterCommand toCommand() {
        return new CreateEncounterCommand(
                patientId,
                practitionerId,
                type,
                priority,
                reason,
                notes,
                startImmediately
        );
    }
}

