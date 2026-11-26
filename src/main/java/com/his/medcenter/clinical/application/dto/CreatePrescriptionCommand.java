package com.his.medcenter.clinical.application.dto;

import java.util.List;
import java.util.UUID;

public record CreatePrescriptionCommand(
        UUID diagnoseId,
        UUID encounterId,
        UUID patientId,
        UUID practitionerId,
        String notes,
        List<CreatePrescriptionItemCommand> items
) {
}