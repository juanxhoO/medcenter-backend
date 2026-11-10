package com.his.medcenter.clinical.domain.model;

import com.his.medcenter.clinical.domain.exception.InvalidEncounterDataException;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate Root representing a Diagnosis in the Hospital Information System.
 *
 * A diagnosis belongs to a patient, is recorded by a practitioner,
 * and is associated with a clinical encounter.
 */
public class Diagnoses {

    private final DiagnosesId id;
    private final UUID patientId;
    private final UUID practitionerId;
    private final UUID encounterId;

    private DiagnosesType type;
    private String description;

    private final Instant createdAt;

    private Diagnoses(
            DiagnosesId id,
            UUID patientId,
            UUID practitionerId,
            UUID encounterId,
            DiagnosesType type,
            String description,
            Instant createdAt
    ) {
        this.id = Objects.requireNonNull(id, "Diagnosis ID cannot be null");
        this.patientId = Objects.requireNonNull(patientId, "Patient ID cannot be null");
        this.practitionerId = Objects.requireNonNull(practitionerId, "Practitioner ID cannot be null");
        this.encounterId = Objects.requireNonNull(encounterId, "Encounter ID cannot be null");
        this.type = Objects.requireNonNull(type, "Diagnosis type cannot be null");
        this.description = validateDescription(description);
        this.createdAt = Objects.requireNonNull(createdAt, "Created timestamp cannot be null");
    }

    /**
     * Factory method for creating a planned diagnosis.
     */
    public static Diagnoses plan(
            DiagnosesId id,
            UUID patientId,
            UUID practitionerId,
            UUID encounterId,
            DiagnosesType type,
            String description
    ) {
        return new Diagnoses(
                id,
                patientId,
                practitionerId,
                encounterId,
                type,
                description,
                Instant.now()
        );
    }

    /**
     * Factory method for creating a diagnosis immediately.
     * Useful for walk-in or emergency encounters.
     */
    public static Diagnoses startNew(
            DiagnosesId id,
            UUID patientId,
            UUID practitionerId,
            UUID encounterId,
            DiagnosesType type,
            String description
    ) {
        return new Diagnoses(
                id,
                patientId,
                practitionerId,
                encounterId,
                type,
                description,
                Instant.now()
        );
    }

    /**
     * Reconstitutes an existing diagnosis from persistence.
     */
    public static Diagnoses reconstitute(
            DiagnosesId id,
            UUID patientId,
            UUID practitionerId,
            UUID encounterId,
            DiagnosesType type,
            String description,
            Instant createdAt
    ) {
        return new Diagnoses(
                id,
                patientId,
                practitionerId,
                encounterId,
                type,
                description,
                createdAt
        );
    }

    // Business Methods

    /**
     * Updates the diagnosis description.
     */
    public void updateDescription(String description) {
        this.description = validateDescription(description);
    }

    /**
     * Changes the diagnosis classification.
     */
    public void changeType(DiagnosesType type) {
        this.type = Objects.requireNonNull(type, "Diagnosis type cannot be null");
    }

    /**
     * Updates the diagnosis information.
     */
    public void update(DiagnosesType type, String description) {
        if (type != null) {
            this.type = type;
        }

        if (description != null) {
            this.description = validateDescription(description);
        }
    }

    // Validation

    private static String validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new InvalidEncounterDataException(
                    "Diagnosis description cannot be empty"
            );
        }

        return description.trim();
    }

    // Getters

    public DiagnosesId getId() {
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

    public DiagnosesType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}