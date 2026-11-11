package com.his.medcenter.clinical.application.dto;

import java.util.List;
import java.util.UUID;

public record ClinicalSummaryDto(
        UUID patientId,
        List<EncounterResponseDto> encounters,
        List<PrescriptionResponseDto> prescriptions
) {
}

