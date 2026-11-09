package com.his.medcenter.clinical.domain.model;

import com.his.medcenter.clinical.domain.exception.EncounterIllegalStateException;
import com.his.medcenter.clinical.domain.exception.InvalidEncounterDataException;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate Root representing a clinical Encounter in the Hospital Information System.
 * Enforces business rules and invariants.
 */
public class Encounter {

    private final EncounterId id;
    private final UUID patientId;
    private final UUID practitionerId;
    private EncounterType type;
    private EncounterStatus status;
    private EncounterPriority priority;
    private String reason;
    private String diagnosis;
    private String notes;
    private Instant startedAt;
    private Instant endedAt;
    private final Instant createdAt;
    private Instant updatedAt;

    private Encounter(
            EncounterId id,
            UUID patientId,
            UUID practitionerId,
            EncounterType type,
            EncounterStatus status,
            EncounterPriority priority,
            String reason,
            String diagnosis,
            String notes,
            Instant startedAt,
            Instant endedAt,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "Encounter ID cannot be null");
        this.patientId = Objects.requireNonNull(patientId, "Patient ID cannot be null");
        this.practitionerId = Objects.requireNonNull(practitionerId, "Practitioner ID cannot be null");
        this.type = Objects.requireNonNull(type, "Encounter type cannot be null");
        this.status = Objects.requireNonNull(status, "Encounter status cannot be null");
        this.priority = Objects.requireNonNull(priority, "Encounter priority cannot be null");
        this.reason = validateNotBlank(reason, "Chief complaint / reason cannot be blank");
        this.diagnosis = diagnosis != null ? diagnosis.trim() : null;
        this.notes = notes != null ? notes.trim() : null;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.createdAt = Objects.requireNonNull(createdAt, "Created timestamp cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated timestamp cannot be null");
    }

    /**
     * Factory method for scheduling / planning a new Encounter.
     */
    public static Encounter plan(
            EncounterId id,
            UUID patientId,
            UUID practitionerId,
            EncounterType type,
            EncounterPriority priority,
            String reason,
            String notes
    ) {
        Instant now = Instant.now();
        return new Encounter(
                id,
                patientId,
                practitionerId,
                type,
                EncounterStatus.PLANNED,
                priority,
                reason,
                null,
                notes,
                null,
                null,
                now,
                now
        );
    }

    /**
     * Factory method for creating an immediately active Encounter (e.g. walk-in or emergency).
     */
    public static Encounter startNew(
            EncounterId id,
            UUID patientId,
            UUID practitionerId,
            EncounterType type,
            EncounterPriority priority,
            String reason,
            String notes
    ) {
        Instant now = Instant.now();
        return new Encounter(
                id,
                patientId,
                practitionerId,
                type,
                EncounterStatus.IN_PROGRESS,
                priority,
                reason,
                null,
                notes,
                now,
                null,
                now,
                now
        );
    }

    /**
     * Reconstitutes an existing Encounter aggregate from persistence.
     */
    public static Encounter reconstitute(
            EncounterId id,
            UUID patientId,
            UUID practitionerId,
            EncounterType type,
            EncounterStatus status,
            EncounterPriority priority,
            String reason,
            String diagnosis,
            String notes,
            Instant startedAt,
            Instant endedAt,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new Encounter(
                id,
                patientId,
                practitionerId,
                type,
                status,
                priority,
                reason,
                diagnosis,
                notes,
                startedAt,
                endedAt,
                createdAt,
                updatedAt
        );
    }

    // Business Methods

    public void start() {
        if (this.status == EncounterStatus.IN_PROGRESS) {
            return;
        }
        if (this.status == EncounterStatus.COMPLETED) {
            throw new EncounterIllegalStateException("Cannot start an already completed encounter");
        }
        if (this.status == EncounterStatus.CANCELLED) {
            throw new EncounterIllegalStateException("Cannot start a cancelled encounter");
        }
        this.status = EncounterStatus.IN_PROGRESS;
        if (this.startedAt == null) {
            this.startedAt = Instant.now();
        }
        this.updatedAt = Instant.now();
    }

    public void complete(String diagnosis, String additionalNotes) {
        if (this.status == EncounterStatus.COMPLETED) {
            return;
        }
        if (this.status == EncounterStatus.CANCELLED) {
            throw new EncounterIllegalStateException("Cannot complete a cancelled encounter");
        }
        if (diagnosis != null && !diagnosis.trim().isEmpty()) {
            this.diagnosis = diagnosis.trim();
        }
        if (additionalNotes != null && !additionalNotes.trim().isEmpty()) {
            this.notes = this.notes == null || this.notes.isEmpty()
                    ? additionalNotes.trim()
                    : this.notes + "\n" + additionalNotes.trim();
        }
        this.status = EncounterStatus.COMPLETED;
        this.endedAt = Instant.now();
        if (this.startedAt == null) {
            this.startedAt = this.endedAt;
        }
        this.updatedAt = Instant.now();
    }

    public void cancel(String cancelReason) {
        if (this.status == EncounterStatus.CANCELLED) {
            return;
        }
        if (this.status == EncounterStatus.COMPLETED) {
            throw new EncounterIllegalStateException("Cannot cancel an already completed encounter");
        }
        String noteSuffix = (cancelReason != null && !cancelReason.trim().isEmpty())
                ? "[Cancelled: " + cancelReason.trim() + "]"
                : "[Cancelled]";
        this.notes = this.notes == null || this.notes.isEmpty()
                ? noteSuffix
                : this.notes + "\n" + noteSuffix;
        this.status = EncounterStatus.CANCELLED;
        this.endedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void updateClinicalDetails(String reason, String diagnosis, String notes, EncounterPriority priority) {
        if (this.status == EncounterStatus.CANCELLED) {
            throw new EncounterIllegalStateException("Cannot update details of a cancelled encounter");
        }
        if (reason != null && !reason.trim().isEmpty()) {
            this.reason = reason.trim();
        }
        if (diagnosis != null) {
            this.diagnosis = diagnosis.trim();
        }
        if (notes != null) {
            this.notes = notes.trim();
        }
        if (priority != null) {
            this.priority = priority;
        }
        this.updatedAt = Instant.now();
    }

    public boolean isActive() {
        return this.status == EncounterStatus.IN_PROGRESS || this.status == EncounterStatus.PLANNED;
    }

    // Getters

    public EncounterId getId() {
        return id;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public UUID getPractitionerId() {
        return practitionerId;
    }

    public EncounterType getType() {
        return type;
    }

    public EncounterStatus getStatus() {
        return status;
    }

    public EncounterPriority getPriority() {
        return priority;
    }

    public String getReason() {
        return reason;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public String getNotes() {
        return notes;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getEndedAt() {
        return endedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    private static String validateNotBlank(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidEncounterDataException(message);
        }
        return value.trim();
    }
}

