package com.his.medcenter.user.domain.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representing a unique identifier for a User.
 */
public record UserId(UUID value) implements Serializable {

    public UserId {
        Objects.requireNonNull(value, "UserId cannot be null");
    }

    public static UserId newUnique() {
        return new UserId(UUID.randomUUID());
    }

    public static UserId fromString(String id) {
        Objects.requireNonNull(id, "ID string cannot be null");
        return new UserId(UUID.fromString(id));
    }

    public static UserId of(UUID id) {
        return new UserId(id);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
