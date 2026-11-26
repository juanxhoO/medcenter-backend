package com.his.medcenter.clinical.domain.model;

import com.his.medcenter.clinical.domain.exception.InvalidPrescriptionDataException;
import com.his.medcenter.clinical.domain.exception.PrescriptionIllegalStateException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate Root representing a medical Prescription.
 *
 * A prescription groups one or more prescribed medication items
 * associated with a patient, practitioner, diagnosis and encounter.
 *
 * PrescriptionItem manages individual medication details and cancellation.
 * Pharmacy manages actual medication dispensation and inventory.
 */
public class Prescription {

    private final PrescriptionId id;
    private final DiagnosesId diagnoseId;
    private final EncounterId encounterId;

    private final UUID patientId;
    private final UUID practitionerId;

    private String notes;

    private final List<PrescriptionItem> items = new ArrayList<>();

    private PrescriptionStatus status;

    private final Instant prescribedAt;
    private final Instant expiresAt;
    private final Instant createdAt;

    private Instant updatedAt;

    private Prescription(
            PrescriptionId id,
            DiagnosesId diagnoseId,
            EncounterId encounterId,
            UUID patientId,
            UUID practitionerId,
            String notes,
            PrescriptionStatus status,
            Instant prescribedAt,
            Instant expiresAt,
            Instant createdAt,
            Instant updatedAt,
            List<PrescriptionItem> items
    ) {
        this.id = Objects.requireNonNull(
                id, "Prescription ID cannot be null"
        );

        this.diagnoseId = diagnoseId;
        this.encounterId = encounterId;

        this.patientId = Objects.requireNonNull(
                patientId, "Patient ID cannot be null"
        );

        this.practitionerId = Objects.requireNonNull(
                practitionerId, "Practitioner ID cannot be null"
        );

        this.notes = notes != null ? notes.trim() : null;

        this.status = Objects.requireNonNull(
                status, "Prescription status cannot be null"
        );

        this.prescribedAt = Objects.requireNonNull(
                prescribedAt, "Prescribed timestamp cannot be null"
        );

        this.expiresAt = Objects.requireNonNull(
                expiresAt, "Expiration timestamp cannot be null"
        );

        this.createdAt = Objects.requireNonNull(
                createdAt, "Created timestamp cannot be null"
        );

        this.updatedAt = Objects.requireNonNull(
                updatedAt, "Updated timestamp cannot be null"
        );

        if (expiresAt.isBefore(prescribedAt)) {
            throw new InvalidPrescriptionDataException(
                    "Expiration cannot be before prescription date"
            );
        }

        if (items != null) {
            for (PrescriptionItem item : items) {
                validateItem(item);
                this.items.add(item);
            }
        }
    }

    // Factory Methods

    /**
     * Creates a new medical prescription.
     */
    public static Prescription create(
            PrescriptionId id,
            DiagnosesId diagnoseId,
            EncounterId encounterId,
            UUID patientId,
            UUID practitionerId,
            String notes
    ) {
        Instant now = Instant.now();

        // Default validity period: 30 days.
        // Should eventually be configurable according to institutional
        // policies and applicable medication regulations.
        Instant expiresAt = now.plus(30, ChronoUnit.DAYS);

        return new Prescription(
                id,
                diagnoseId,
                encounterId,
                patientId,
                practitionerId,
                notes,
                PrescriptionStatus.ACTIVE,
                now,
                expiresAt,
                now,
                now,
                Collections.emptyList()
        );
    }

    /**
     * Reconstitutes an existing Prescription aggregate from persistence.
     */
    public static Prescription reconstitute(
            PrescriptionId id,
            DiagnosesId diagnoseId,
            EncounterId encounterId,
            UUID patientId,
            UUID practitionerId,
            String notes,
            PrescriptionStatus status,
            Instant prescribedAt,
            Instant expiresAt,
            Instant createdAt,
            Instant updatedAt,
            List<PrescriptionItem> items
    ) {
        return new Prescription(
                id,
                diagnoseId,
                encounterId,
                patientId,
                practitionerId,
                notes,
                status,
                prescribedAt,
                expiresAt,
                createdAt,
                updatedAt,
                items
        );
    }

    // Business Methods

    /**
     * Adds a medication item to the prescription.
     */
    public void addItem(PrescriptionItem item) {
        ensureModifiable();
        validateItem(item);

        boolean alreadyExists = items.stream()
                .anyMatch(existing ->
                        existing.getId().equals(item.getId())
                );

        if (alreadyExists) {
            throw new InvalidPrescriptionDataException(
                    "Prescription item already exists"
            );
        }

        items.add(item);
        this.updatedAt = Instant.now();
    }

    /**
     * Cancels an individual medication item.
     *
     * The item remains in the prescription for clinical auditing.
     */
    public void cancelItem(
            PrescriptionItemId itemId,
            String reason
    ) {
        ensureModifiable();

        PrescriptionItem item = findItem(itemId);

        if (!item.isActive()) {
            throw new PrescriptionIllegalStateException(
                    "Cannot cancel an inactive prescription item"
            );
        }

        item.cancel(reason);

        this.updatedAt = Instant.now();
    }

    /**
     * Cancels the entire prescription and all active medication items.
     */
    public void cancel(String reason) {
        if (this.status == PrescriptionStatus.CANCELLED) {
            return;
        }

        if (this.status == PrescriptionStatus.DISPENSED) {
            throw new PrescriptionIllegalStateException(
                    "Cannot cancel an already dispensed prescription"
            );
        }

        if (this.status == PrescriptionStatus.EXPIRED) {
            throw new PrescriptionIllegalStateException(
                    "Cannot cancel an expired prescription"
            );
        }

        // Cancel all active medication items.
        this.items.stream()
                .filter(PrescriptionItem::isActive)
                .forEach(item -> item.cancel(reason));

        this.status = PrescriptionStatus.CANCELLED;

        if (reason != null && !reason.isBlank()) {
            String suffix = "[Cancelled: " + reason.trim() + "]";

            this.notes = this.notes == null || this.notes.isBlank()
                    ? suffix
                    : this.notes + " " + suffix;
        }

        this.updatedAt = Instant.now();
    }

    /**
     * Marks the prescription as fully dispensed.
     *
     * This operation should only be called after Pharmacy confirms
     * that all active medication items have been fully dispensed.
     */
    public void markAsDispensed() {
        if (this.status == PrescriptionStatus.DISPENSED) {
            return;
        }

        if (this.status == PrescriptionStatus.CANCELLED) {
            throw new PrescriptionIllegalStateException(
                    "Cannot dispense a cancelled prescription"
            );
        }

        if (isExpired()) {
            throw new PrescriptionIllegalStateException(
                    "Cannot dispense an expired prescription"
            );
        }

        if (items.isEmpty()) {
            throw new PrescriptionIllegalStateException(
                    "Cannot dispense a prescription without items"
            );
        }

        if (items.stream().noneMatch(PrescriptionItem::isActive)) {
            throw new PrescriptionIllegalStateException(
                    "Prescription has no active items"
            );
        }

        this.status = PrescriptionStatus.DISPENSED;
        this.updatedAt = Instant.now();
    }

    /**
     * Finds a medication item inside this prescription.
     */
    private PrescriptionItem findItem(PrescriptionItemId itemId) {
        Objects.requireNonNull(
                itemId, "Prescription item ID cannot be null"
        );

        return items.stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new InvalidPrescriptionDataException(
                        "Prescription item not found"
                ));
    }

    /**
     * Validates that the medication item belongs to this prescription.
     */
    private void validateItem(PrescriptionItem item) {
        Objects.requireNonNull(
                item, "Prescription item cannot be null"
        );

        if (!this.id.equals(item.getPrescriptionId())) {
            throw new InvalidPrescriptionDataException(
                    "Prescription item belongs to another prescription"
            );
        }
    }

    /**
     * Ensures the prescription can still be modified.
     */
    private void ensureModifiable() {
        if (this.status != PrescriptionStatus.ACTIVE) {
            throw new PrescriptionIllegalStateException(
                    "Cannot modify a prescription that is not active"
            );
        }

        if (isExpired()) {
            throw new PrescriptionIllegalStateException(
                    "Cannot modify an expired prescription"
            );
        }
    }

    // State Queries

    public boolean isExpired() {
        return !Instant.now().isBefore(expiresAt);
    }

    public boolean isActive() {
        return this.status == PrescriptionStatus.ACTIVE
                && !isExpired();
    }

    public boolean hasActiveItems() {
        return items.stream().anyMatch(PrescriptionItem::isActive);
    }

    // Getters

    public PrescriptionId getId() {
        return id;
    }

    public DiagnosesId getDiagnoseId() {
        return diagnoseId;
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

    public String getNotes() {
        return notes;
    }

    public List<PrescriptionItem> getItems() {
        return Collections.unmodifiableList(items);
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
}