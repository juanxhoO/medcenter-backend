package com.his.medcenter.clinical.domain.model;

import com.his.medcenter.clinical.domain.exception.InvalidPrescriptionDataException;
import com.his.medcenter.clinical.domain.exception.PrescriptionIllegalStateException;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Entity representing an individual medication item within a medical prescription.
 *
 * A PrescriptionItem can reference an institutional Medication catalog entry
 * or represent an externally prescribed medication.
 *
 * Pharmacy inventory and dispensation are managed independently.
 */
public class PrescriptionItem {

    private final PrescriptionItemId id;
    private final PrescriptionId prescriptionId;

    /**
     * Reference to the institutional medication catalog.
     * Null when prescribing an external medication.
     */
    private final UUID medicationId;

    /**
     * Snapshot of the medication name at prescription time.
     */
    private final String medicationName;
    private final String dosage;
    private final String frequency;
    private final String route;
    private final int durationDays;
    private final int quantity;
    private final String instructions;

    private PrescriptionItemStatus status;

    /**
     * Cancellation audit information.
     */
    private String cancellationReason;
    private Instant cancelledAt;

    private final Instant createdAt;
    private Instant updatedAt;

    private PrescriptionItem(
            PrescriptionItemId id,
            PrescriptionId prescriptionId,
            UUID medicationId,
            String medicationName,
            String dosage,
            String frequency,
            String route,
            int durationDays,
            int quantity,
            String instructions,
            PrescriptionItemStatus status,
            String cancellationReason,
            Instant cancelledAt,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(
                id, "Prescription item ID cannot be null"
        );

        this.prescriptionId = Objects.requireNonNull(
                prescriptionId, "Prescription ID cannot be null"
        );

        this.medicationId = medicationId;

        this.medicationName = validateNotBlank(
                medicationName, "Medication name cannot be blank"
        );

        this.dosage = validateNotBlank(
                dosage, "Dosage cannot be blank"
        );

        this.frequency = validateNotBlank(
                frequency, "Frequency cannot be blank"
        );

        this.route = validateNotBlank(
                route, "Administration route cannot be blank"
        );

        if (durationDays <= 0) {
            throw new InvalidPrescriptionDataException(
                    "Duration must be at least 1 day"
            );
        }

        if (quantity <= 0) {
            throw new InvalidPrescriptionDataException(
                    "Quantity must be greater than zero"
            );
        }

        this.durationDays = durationDays;
        this.quantity = quantity;

        this.instructions = instructions != null
                ? instructions.trim()
                : null;

        this.status = Objects.requireNonNull(
                status, "Prescription item status cannot be null"
        );

        this.cancellationReason = cancellationReason;
        this.cancelledAt = cancelledAt;

        this.createdAt = Objects.requireNonNull(
                createdAt, "Created timestamp cannot be null"
        );

        this.updatedAt = Objects.requireNonNull(
                updatedAt, "Updated timestamp cannot be null"
        );

        validateCancellationState();
    }

    // Factory Methods

    /**
     * Creates an item referencing the institutional medication catalog.
     */
    public static PrescriptionItem create(
            PrescriptionItemId id,
            PrescriptionId prescriptionId,
            UUID medicationId,
            String medicationName,
            String dosage,
            String frequency,
            String route,
            int durationDays,
            int quantity,
            String instructions
    ) {
        Instant now = Instant.now();

        return new PrescriptionItem(
                id,
                prescriptionId,
                Objects.requireNonNull(
                        medicationId, "Medication ID cannot be null"
                ),
                medicationName,
                dosage,
                frequency,
                route,
                durationDays,
                quantity,
                instructions,
                PrescriptionItemStatus.ACTIVE,
                null,
                null,
                now,
                now
        );
    }

    /**
     * Creates an item for an external medication.
     */
    public static PrescriptionItem createExternal(
            PrescriptionItemId id,
            PrescriptionId prescriptionId,
            String medicationName,
            String dosage,
            String frequency,
            String route,
            int durationDays,
            int quantity,
            String instructions
    ) {
        Instant now = Instant.now();

        return new PrescriptionItem(
                id,
                prescriptionId,
                null,
                medicationName,
                dosage,
                frequency,
                route,
                durationDays,
                quantity,
                instructions,
                PrescriptionItemStatus.ACTIVE,
                null,
                null,
                now,
                now
        );
    }

    /**
     * Reconstitutes an existing PrescriptionItem from persistence.
     */
    public static PrescriptionItem reconstitute(
            PrescriptionItemId id,
            PrescriptionId prescriptionId,
            UUID medicationId,
            String medicationName,
            String dosage,
            String frequency,
            String route,
            int durationDays,
            int quantity,
            String instructions,
            PrescriptionItemStatus status,
            String cancellationReason,
            Instant cancelledAt,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new PrescriptionItem(
                id,
                prescriptionId,
                medicationId,
                medicationName,
                dosage,
                frequency,
                route,
                durationDays,
                quantity,
                instructions,
                status,
                cancellationReason,
                cancelledAt,
                createdAt,
                updatedAt
        );
    }

    // Business Methods

    /**
     * Cancels this medication item.
     *
     * The item is not physically deleted because its history
     * must remain available for clinical auditing.
     */
    public void cancel(String reason) {

        if (!isActive()) {
            throw new PrescriptionIllegalStateException(
                    "Only active prescription items can be cancelled"
            );
        }

        if (reason == null || reason.isBlank()) {
            throw new InvalidPrescriptionDataException(
                    "Cancellation reason cannot be blank"
            );
        }

        this.status = PrescriptionItemStatus.CANCELLED;
        this.cancellationReason = reason.trim();
        this.cancelledAt = Instant.now();
        this.updatedAt = this.cancelledAt;
    }

    /**
     * Checks whether the item is currently active.
     */
    public boolean isActive() {
        return this.status == PrescriptionItemStatus.ACTIVE;
    }

    /**
     * Checks whether the item references an institutional medication.
     */
    public boolean isCatalogMedication() {
        return this.medicationId != null;
    }

    /**
     * Checks whether the item represents an external medication.
     */
    public boolean isExternalMedication() {
        return this.medicationId == null;
    }

    /**
     * Validates cancellation information when reconstituting the entity.
     */
    private void validateCancellationState() {

        if (status == PrescriptionItemStatus.CANCELLED) {

            if (cancelledAt == null) {
                throw new InvalidPrescriptionDataException(
                        "Cancelled items must have a cancellation timestamp"
                );
            }

            if (cancellationReason == null || cancellationReason.isBlank()) {
                throw new InvalidPrescriptionDataException(
                        "Cancelled items must have a cancellation reason"
                );
            }

        } else if (cancellationReason != null || cancelledAt != null) {

            throw new InvalidPrescriptionDataException(
                    "Active items cannot have cancellation information"
            );
        }
    }

    private static String validateNotBlank(
            String value,
            String message
    ) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidPrescriptionDataException(message);
        }

        return value.trim();
    }

    // Getters

    public PrescriptionItemId getId() {
        return id;
    }

    public PrescriptionId getPrescriptionId() {
        return prescriptionId;
    }

    public UUID getMedicationId() {
        return medicationId;
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

    public int getQuantity() {
        return quantity;
    }

    public String getInstructions() {
        return instructions;
    }

    public PrescriptionItemStatus getStatus() {
        return status;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public Instant getCancelledAt() {
        return cancelledAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}