package com.his.medcenter.clinical.domain.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representing a unique identifier for a Prescription.
 */
public record VitalSignsId(UUID value) implements Serializable {

    public VitalSignsId {
        Objects.requireNonNull(value, "VitalSignsId cannot be null");
    }

    public static VitalSignsId newUnique() {
        return new VitalSignsId(UUID.randomUUID());
    }

    public static VitalSignsId fromString(String id) {
        Objects.requireNonNull(id, "ID string cannot be null");
        return new VitalSignsId(UUID.fromString(id));
    }

    public static VitalSignsId of(UUID id) {
        return new VitalSignsId(id);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

