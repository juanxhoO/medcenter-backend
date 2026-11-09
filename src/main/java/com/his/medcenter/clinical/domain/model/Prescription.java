package com.his.medcenter.clinical.domain.model;

import com.his.medcenter.clinical.domain.exception.InvalidPrescriptionDataException;
import com.his.medcenter.clinical.domain.exception.PrescriptionIllegalStateException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate Root representing a medical Prescription in the Hospital Information System.
 * Enforces business rules and invariants.
 */
public class Prescription {

    private final PrescriptionId id;
    private final EncounterId encounterId;
    private final UUID patientId;
    private final UUID practitionerId;
    private final String medicationName;
    private final String dosage;
    private final String frequency;
    private final String route;
    private final int durationDays;
    private String instructions;
    private PrescriptionStatus status;
    private final Instant prescribedAt;
    private final Instant expiresAt;
    private final Instant createdAt;
    private Instant updatedAt;

    private Prescription(
            PrescriptionId id,
            EncounterId encounterId,
            UUID patientId,
            UUID practitionerId,
            String medicationName,
            String dosage,
            String frequency,
            String route,
            int durationDays,
            String instructions,
            PrescriptionStatus status,
            Instant prescribedAt,
            Instant expiresAt,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "Prescription ID cannot be null");
        this.encounterId = encounterId; // Optional link to an encounter
        this.patientId = Objects.requireNonNull(patientId, "Patient ID cannot be null");
        this.practitionerId = Objects.requireNonNull(practitionerId, "Practitioner ID cannot be null");
        this.medicationName = validateNotBlank(medicationName, "Medication name cannot be blank");
        this.dosage = validateNotBlank(dosage, "Dosage cannot be blank");
        this.frequency = validateNotBlank(frequency, "Frequency cannot be blank");
        this.route = route != null ? route.trim() : "Oral";
        if (durationDays <= 0) {
            throw new InvalidPrescriptionDataException("Duration must be at least 1 day");
        }
        this.durationDays = durationDays;
        this.instructions = instructions != null ? instructions.trim() : null;
        this.status = Objects.requireNonNull(status, "Prescription status cannot be null");
        this.prescribedAt = Objects.requireNonNull(prescribedAt, "Prescribed timestamp cannot be null");
        this.expiresAt = Objects.requireNonNull(expiresAt, "Expiration timestamp cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Created timestamp cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated timestamp cannot be null");
    }

    /**
     * Factory method for creating a newly issued prescription.
     */
    public static Prescription create(
            PrescriptionId id,
            EncounterId encounterId,
            UUID patientId,
            UUID practitionerId,
            String medicationName,
            String dosage,
            String frequency,
            String route,
            int durationDays,
            String instructions
    ) {
        Instant now = Instant.now();
        // Prescriptions default to valid for the treatment duration plus a 30-day grace period
        Instant expiresAt = now.plus(durationDays + 30L, ChronoUnit.DAYS);
        return new Prescription(
                id,
                encounterId,
                patientId,
                practitionerId,
                medicationName,
                dosage,
                frequency,
                route,
                durationDays,
                instructions,
                PrescriptionStatus.ACTIVE,
                now,
                expiresAt,
                now,
                now
        );
    }

    /**
     * Reconstitutes an existing Prescription aggregate from persistence.
     */
    public static Prescription reconstitute(
            PrescriptionId id,
            EncounterId encounterId,
            UUID patientId,
            UUID practitionerId,
            String medicationName,
            String dosage,
            String frequency,
            String route,
            int durationDays,
            String instructions,
            PrescriptionStatus status,
            Instant prescribedAt,
            Instant expiresAt,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new Prescription(
                id,
                encounterId,
                patientId,
                practitionerId,
                medicationName,
                dosage,
                frequency,
                route,
                durationDays,
                instructions,
                status,
                prescribedAt,
                expiresAt,
                createdAt,
                updatedAt
        );
    }

    // Business Methods

    public void dispense() {
        if (this.status == PrescriptionStatus.DISPENSED) {
            return;
        }
        if (this.status == PrescriptionStatus.CANCELLED) {
            throw new PrescriptionIllegalStateException("Cannot dispense a cancelled prescription");
        }
        if (isExpired()) {
            this.status = PrescriptionStatus.EXPIRED;
            this.updatedAt = Instant.now();
            throw new PrescriptionIllegalStateException("Cannot dispense an expired prescription");
        }
        this.status = PrescriptionStatus.DISPENSED;
        this.updatedAt = Instant.now();
    }

    public void cancel(String cancelReason) {
        if (this.status == PrescriptionStatus.CANCELLED) {
            return;
        }
        if (this.status == PrescriptionStatus.DISPENSED) {
            throw new PrescriptionIllegalStateException("Cannot cancel an already dispensed prescription");
        }
        String noteSuffix = (cancelReason != null && !cancelReason.trim().isEmpty())
                ? " [Cancelled: " + cancelReason.trim() + "]"
                : " [Cancelled]";
        this.instructions = this.instructions == null || this.instructions.isEmpty()
                ? noteSuffix
                : this.instructions + noteSuffix;
        this.status = PrescriptionStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isActive() {
        return this.status == PrescriptionStatus.ACTIVE && !isExpired();
    }

    // Getters

    public PrescriptionId getId() {
        return id;
    }

    public EncounterId getEncounterId() {
        return encounterId;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public UUID getPractitionerId() {
        return practitionerId;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public String getDosage() {
        return dosage;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getRoute() {
        return route;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public String getInstructions() {
        return instructions;
    }

    public PrescriptionStatus getStatus() {
        if (this.status == PrescriptionStatus.ACTIVE && isExpired()) {
            return PrescriptionStatus.EXPIRED;
        }
        return status;
    }

    public Instant getPrescribedAt() {
        return prescribedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    private static String validateNotBlank(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidPrescriptionDataException(message);
        }
        return value.trim();
    }
}

