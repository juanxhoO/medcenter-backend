package com.his.medcenter.patient.domain.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representing a unique identifier for a Patient.
 */
public record PatientId(UUID value) implements Serializable {

    public PatientId {
        Objects.requireNonNull(value, "PatientId cannot be null");
    }

    public static PatientId newUnique() {
        return new PatientId(UUID.randomUUID());
    }

    public static PatientId fromString(String id) {
        Objects.requireNonNull(id, "ID string cannot be null");
        return new PatientId(UUID.fromString(id));
    }

    public static PatientId of(UUID id) {
        return new PatientId(id);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

