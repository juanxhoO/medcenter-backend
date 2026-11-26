package com.his.medcenter.clinical.domain.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representing a unique identifier for a PrescriptionItem.
 */
public record PrescriptionItemId(UUID value) implements Serializable {

    public PrescriptionItemId {
        Objects.requireNonNull(value, "PrescriptionItemId cannot be null");
    }

    public static PrescriptionItemId newUnique() {
        return new PrescriptionItemId(UUID.randomUUID());
    }

    public static PrescriptionItemId fromString(String id) {
        Objects.requireNonNull(id, "ID string cannot be null");
        return new PrescriptionItemId(UUID.fromString(id));
    }

    public static PrescriptionItemId of(UUID id) {
        return new PrescriptionItemId(id);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

