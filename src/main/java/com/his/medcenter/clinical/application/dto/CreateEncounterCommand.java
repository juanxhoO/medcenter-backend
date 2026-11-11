package com.his.medcenter.clinical.application.dto;

import com.his.medcenter.clinical.domain.model.EncounterPriority;
import com.his.medcenter.clinical.domain.model.EncounterType;

import java.util.UUID;

public record CreateEncounterCommand(
        UUID patientId,
        UUID practitionerId,
        EncounterType type,
        EncounterPriority priority,
        String reason,
        String notes,
        boolean startImmediately
) {
}

