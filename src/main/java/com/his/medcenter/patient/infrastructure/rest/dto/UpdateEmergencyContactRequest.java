package com.his.medcenter.patient.infrastructure.rest.dto;

import com.his.medcenter.patient.application.dto.UpdateEmergencyContactCommand;

public record UpdateEmergencyContactRequest(
        String contactName,
        String contactPhone,
        String contactRelationship
) {
    public UpdateEmergencyContactCommand toCommand() {
        return new UpdateEmergencyContactCommand(
                contactName,
                contactPhone,
                contactRelationship
        );
    }
}

