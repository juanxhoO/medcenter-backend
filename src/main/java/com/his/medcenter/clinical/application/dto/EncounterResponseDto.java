package com.his.medcenter.clinical.application.dto;

import com.his.medcenter.clinical.domain.model.Encounter;
import com.his.medcenter.clinical.domain.model.EncounterPriority;
import com.his.medcenter.clinical.domain.model.EncounterStatus;
import com.his.medcenter.clinical.domain.model.EncounterType;

import java.time.Instant;
import java.util.UUID;

public record EncounterResponseDto(
        UUID id,
        UUID patientId,
        UUID practitionerId,
        EncounterType type,
        EncounterStatus status,
        EncounterPriority priority,
        String reason,
        String diagnosis,
        String notes,
        Instant startedAt,
        Instant endedAt,
        Instant createdAt,
        Instant updatedAt
) {
    public static EncounterResponseDto fromDomain(Encounter encounter) {
        return new EncounterResponseDto(
                encounter.getId().value(),
                encounter.getPatientId(),
                encounter.getPractitionerId(),
                encounter.getType(),
                encounter.getStatus(),
                encounter.getPriority(),
                encounter.getReason(),
                encounter.getDiagnosis(),
                encounter.getNotes(),
                encounter.getStartedAt(),
                encounter.getEndedAt(),
                encounter.getCreatedAt(),
                encounter.getUpdatedAt()
        );
    }
}

