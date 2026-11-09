package com.his.medcenter.clinical.application.dto;

public record CompleteEncounterCommand(
        String diagnosis,
        String notes
) {
}

