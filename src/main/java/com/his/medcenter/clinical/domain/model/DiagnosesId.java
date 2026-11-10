package com.his.medcenter.clinical.domain.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representing a unique identifier for an Encounter.
 */
public record DiagnosesId(UUID value) implements Serializable {

    public DiagnosesId {
        Objects.requireNonNull(value, "EncounterId cannot be null");
    }

    public static DiagnosesId newUnique() {
        return new DiagnosesId(UUID.randomUUID());
    }

    public static DiagnosesId fromString(String id) {
        Objects.requireNonNull(id, "ID string cannot be null");
        return new DiagnosesId(UUID.fromString(id));
    }

    public static DiagnosesId of(UUID id) {
        return new DiagnosesId(id);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

