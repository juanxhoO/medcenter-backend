package com.his.medcenter.clinical.domain.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representing a unique identifier for an Encounter.
 */
public record EncounterId(UUID value) implements Serializable {

    public EncounterId {
        Objects.requireNonNull(value, "EncounterId cannot be null");
    }

    public static EncounterId newUnique() {
        return new EncounterId(UUID.randomUUID());
    }

    public static EncounterId fromString(String id) {
        Objects.requireNonNull(id, "ID string cannot be null");
        return new EncounterId(UUID.fromString(id));
    }

    public static EncounterId of(UUID id) {
        return new EncounterId(id);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

