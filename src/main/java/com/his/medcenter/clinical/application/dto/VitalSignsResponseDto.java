package com.his.medcenter.clinical.application.dto;

import com.his.medcenter.clinical.domain.model.VitalSigns;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record VitalSignsResponseDto(
        UUID id,
        UUID patientId,
        UUID practitionerId,
        UUID encounterId,
        BigDecimal temperature,
        Integer systolicBloodPressure,
        Integer diastolicBloodPressure,
        Integer heartRate,
        Integer respiratoryRate,
        BigDecimal oxygenSaturation,
        BigDecimal height,
        BigDecimal weight,
        String bloodPressure,
        BigDecimal bmi,
        Instant createdAt
) {
    public static VitalSignsResponseDto fromDomain(VitalSigns vitalSigns) {
        return new VitalSignsResponseDto(
                vitalSigns.getId().value(),
                vitalSigns.getPatientId(),
                vitalSigns.getPractitionerId(),
                vitalSigns.getEncounterId(),
                vitalSigns.getTemperature(),
                vitalSigns.getSystolicBloodPressure(),
                vitalSigns.getDiastolicBloodPressure(),
                vitalSigns.getHeartRate(),
                vitalSigns.getRespiratoryRate(),
                vitalSigns.getOxygenSaturation(),
                vitalSigns.getHeight(),
                vitalSigns.getWeight(),
                vitalSigns.getBloodPressure(),
                vitalSigns.calculateBmi(),
                vitalSigns.getCreatedAt()
        );
    }
}