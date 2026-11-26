package com.his.medcenter.clinical.infrastructure.rest.dto;

import com.his.medcenter.clinical.application.dto.CreatePrescriptionCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreatePrescriptionRequest(

        UUID diagnoseId,

        UUID encounterId,

        @NotNull(message = "Patient ID is required")
        UUID patientId,

        @NotNull(message = "Practitioner ID is required")
        UUID practitionerId,

        String notes,

        @NotEmpty(message = "At least one prescription item is required")
        List<@Valid CreatePrescriptionItemRequest> items

) {

    public CreatePrescriptionCommand toCommand() {
        return new CreatePrescriptionCommand(
                diagnoseId,
                encounterId,
                patientId,
                practitionerId,
                notes,
                items.stream()
                        .map(CreatePrescriptionItemRequest::toCommand)
                        .toList()
        );
    }
}