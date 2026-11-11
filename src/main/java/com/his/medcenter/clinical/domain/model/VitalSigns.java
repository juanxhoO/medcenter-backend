package com.his.medcenter.clinical.domain.model;

import com.his.medcenter.clinical.domain.exception.InvalidEncounterDataException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate Root representing Vital Signs in the Hospital Information System.
 *
 * Vital signs belong to a patient, are recorded by a practitioner,
 * and can be associated with a clinical encounter.
 */
public class VitalSigns {

    private final VitalSignsId id;
    private final UUID patientId;
    private final UUID practitionerId;
    private final UUID encounterId;
    private BigDecimal temperature;
    private Integer systolicBloodPressure;
    private Integer diastolicBloodPressure;
    private Integer heartRate;
    private Integer respiratoryRate;
    private BigDecimal oxygenSaturation;
    private BigDecimal height;
    private BigDecimal weight;

    private final Instant createdAt;

    private VitalSigns(
            VitalSignsId id,
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
            Instant createdAt
    ) {
        this.id = Objects.requireNonNull(id, "Vital signs ID cannot be null");
        this.patientId = Objects.requireNonNull(patientId, "Patient ID cannot be null");
        this.practitionerId = Objects.requireNonNull(practitionerId, "Practitioner ID cannot be null");
        this.encounterId = Objects.requireNonNull(encounterId, "Encounter ID cannot be null");

        validateMeasurements(
                temperature,
                systolicBloodPressure,
                diastolicBloodPressure,
                heartRate,
                respiratoryRate,
                oxygenSaturation,
                height,
                weight
        );

        this.temperature = temperature;
        this.systolicBloodPressure = systolicBloodPressure;
        this.diastolicBloodPressure = diastolicBloodPressure;
        this.heartRate = heartRate;
        this.respiratoryRate = respiratoryRate;
        this.oxygenSaturation = oxygenSaturation;
        this.height = height;
        this.weight = weight;

        this.createdAt = Objects.requireNonNull(createdAt, "Created timestamp cannot be null");
    }

    /**
     * Factory method for recording new vital signs.
     */
    public static VitalSigns record(
            VitalSignsId id,
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
            BigDecimal weight
    ) {
        return new VitalSigns(
                id,
                patientId,
                practitionerId,
                encounterId,
                temperature,
                systolicBloodPressure,
                diastolicBloodPressure,
                heartRate,
                respiratoryRate,
                oxygenSaturation,
                height,
                weight,
                Instant.now()
        );
    }

    /**
     * Reconstitutes existing vital signs from persistence.
     */
    public static VitalSigns reconstitute(
            VitalSignsId id,
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
            Instant createdAt
    ) {
        return new VitalSigns(
                id,
                patientId,
                practitionerId,
                encounterId,
                temperature,
                systolicBloodPressure,
                diastolicBloodPressure,
                heartRate,
                respiratoryRate,
                oxygenSaturation,
                height,
                weight,
                createdAt
        );
    }

    // Business Methods

    /**
     * Updates the recorded vital signs.
     * Null values represent measurements that should remain unchanged.
     */
    public void update(
            BigDecimal temperature,
            Integer systolicBloodPressure,
            Integer diastolicBloodPressure,
            Integer heartRate,
            Integer respiratoryRate,
            BigDecimal oxygenSaturation,
            BigDecimal height,
            BigDecimal weight
    ) {
        BigDecimal newTemperature = temperature != null ? temperature : this.temperature;
        Integer newSystolic = systolicBloodPressure != null
                ? systolicBloodPressure : this.systolicBloodPressure;
        Integer newDiastolic = diastolicBloodPressure != null
                ? diastolicBloodPressure : this.diastolicBloodPressure;
        Integer newHeartRate = heartRate != null ? heartRate : this.heartRate;
        Integer newRespiratoryRate = respiratoryRate != null
                ? respiratoryRate : this.respiratoryRate;
        BigDecimal newOxygenSaturation = oxygenSaturation != null
                ? oxygenSaturation : this.oxygenSaturation;
        BigDecimal newHeight = height != null ? height : this.height;
        BigDecimal newWeight = weight != null ? weight : this.weight;

        validateMeasurements(
                newTemperature,
                newSystolic,
                newDiastolic,
                newHeartRate,
                newRespiratoryRate,
                newOxygenSaturation,
                newHeight,
                newWeight
        );

        this.temperature = newTemperature;
        this.systolicBloodPressure = newSystolic;
        this.diastolicBloodPressure = newDiastolic;
        this.heartRate = newHeartRate;
        this.respiratoryRate = newRespiratoryRate;
        this.oxygenSaturation = newOxygenSaturation;
        this.height = newHeight;
        this.weight = newWeight;
    }

    /**
     * Returns blood pressure formatted as systolic/diastolic.
     */
    public String getBloodPressure() {
        if (systolicBloodPressure == null || diastolicBloodPressure == null) {
            return null;
        }

        return systolicBloodPressure + "/" + diastolicBloodPressure;
    }

    /**
     * Calculates Body Mass Index (BMI) when height and weight are available.
     */
    public BigDecimal calculateBmi() {
        if (height == null || weight == null) {
            return null;
        }

        BigDecimal heightInMeters = height.divide(BigDecimal.valueOf(100));

        return weight.divide(
                heightInMeters.multiply(heightInMeters),
                2,
                java.math.RoundingMode.HALF_UP
        );
    }

    // Validation

    private static void validateMeasurements(
            BigDecimal temperature,
            Integer systolicBloodPressure,
            Integer diastolicBloodPressure,
            Integer heartRate,
            Integer respiratoryRate,
            BigDecimal oxygenSaturation,
            BigDecimal height,
            BigDecimal weight
    ) {
        if (temperature == null
                && systolicBloodPressure == null
                && diastolicBloodPressure == null
                && heartRate == null
                && respiratoryRate == null
                && oxygenSaturation == null
                && height == null
                && weight == null) {
            throw new InvalidEncounterDataException(
                    "At least one vital sign measurement must be provided"
            );
        }

        if (temperature != null && temperature.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidEncounterDataException("Temperature must be greater than zero");
        }

        if ((systolicBloodPressure == null) != (diastolicBloodPressure == null)) {
            throw new InvalidEncounterDataException(
                    "Both systolic and diastolic blood pressure must be provided together"
            );
        }

        if (systolicBloodPressure != null && systolicBloodPressure <= 0) {
            throw new InvalidEncounterDataException("Systolic blood pressure must be positive");
        }

        if (diastolicBloodPressure != null && diastolicBloodPressure <= 0) {
            throw new InvalidEncounterDataException("Diastolic blood pressure must be positive");
        }

        if (heartRate != null && heartRate <= 0) {
            throw new InvalidEncounterDataException("Heart rate must be positive");
        }

        if (respiratoryRate != null && respiratoryRate <= 0) {
            throw new InvalidEncounterDataException("Respiratory rate must be positive");
        }

        if (oxygenSaturation != null &&
                (oxygenSaturation.compareTo(BigDecimal.ZERO) < 0
                        || oxygenSaturation.compareTo(BigDecimal.valueOf(100)) > 0)) {
            throw new InvalidEncounterDataException(
                    "Oxygen saturation must be between 0 and 100"
            );
        }

        if (height != null && height.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidEncounterDataException("Height must be greater than zero");
        }

        if (weight != null && weight.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidEncounterDataException("Weight must be greater than zero");
        }
    }

    // Getters

    public VitalSignsId getId() {
        return id;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public UUID getPractitionerId() {
        return practitionerId;
    }

    public UUID getEncounterId() {
        return encounterId;
    }

    public BigDecimal getTemperature() {
        return temperature;
    }

    public Integer getSystolicBloodPressure() {
        return systolicBloodPressure;
    }

    public Integer getDiastolicBloodPressure() {
        return diastolicBloodPressure;
    }

    public Integer getHeartRate() {
        return heartRate;
    }

    public Integer getRespiratoryRate() {
        return respiratoryRate;
    }

    public BigDecimal getOxygenSaturation() {
        return oxygenSaturation;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}