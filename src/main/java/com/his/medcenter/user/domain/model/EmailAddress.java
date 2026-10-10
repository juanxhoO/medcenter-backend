package com.his.medcenter.user.domain.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object enforcing valid and normalized email addresses.
 */
public record EmailAddress(String value) implements Serializable {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public EmailAddress {
        Objects.requireNonNull(value, "Email value cannot be null");
        String trimmed = value.trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + value);
        }
        value = trimmed;
    }

    public static EmailAddress of(String email) {
        return new EmailAddress(email);
    }

    @Override
    public String toString() {
        return value;
    }
}
