package com.his.medcenter.clinical.domain.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representing a unique identifier for a Prescription.
 */
public record PrescriptionId(UUID value) implements Serializable {

    public PrescriptionId {
        Objects.requireNonNull(value, "PrescriptionId cannot be null");
    }

    public static PrescriptionId newUnique() {
        return new PrescriptionId(UUID.randomUUID());
    }

    public static PrescriptionId fromString(String id) {
        Objects.requireNonNull(id, "ID string cannot be null");
        return new PrescriptionId(UUID.fromString(id));
    }

    public static PrescriptionId of(UUID id) {
        return new PrescriptionId(id);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

