package com.his.medcenter.patient.application.dto;

public record UpdateEmergencyContactCommand(
        String contactName,
        String contactPhone,
        String contactRelationship
) {
}

