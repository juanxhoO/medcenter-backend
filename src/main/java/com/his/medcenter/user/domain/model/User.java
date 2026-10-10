package com.his.medcenter.user.domain.model;

import com.his.medcenter.user.domain.exception.InvalidUserDataException;

import java.time.Instant;
import java.util.Objects;

/**
 * Aggregate Root representing a User in the Hospital Information System.
 * Enforces business rules and invariants.
 */
public class User {

    private final UserId id;
    private final EmailAddress email;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private String phone;
    private UserRole role;
    private UserStatus status;
    private String department;
    private final Instant createdAt;
    private Instant updatedAt;

    private User(
            UserId id,
            EmailAddress email,
            String passwordHash,
            String firstName,
            String lastName,
            String phone,
            UserRole role,
            UserStatus status,
            String department,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "User ID cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.passwordHash = Objects.requireNonNull(passwordHash, "Password hash cannot be null");
        this.firstName = validateNotBlank(firstName, "First name cannot be blank");
        this.lastName = validateNotBlank(lastName, "Last name cannot be blank");
        this.phone = phone != null ? phone.trim() : null;
        this.role = Objects.requireNonNull(role, "User role cannot be null");
        this.status = Objects.requireNonNull(status, "User status cannot be null");
        this.department = department != null ? department.trim() : null;
        this.createdAt = Objects.requireNonNull(createdAt, "Created timestamp cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated timestamp cannot be null");
    }

    /**
     * Factory method for creating a new User aggregate.
     */
    public static User register(
            UserId id,
            EmailAddress email,
            String passwordHash,
            String firstName,
            String lastName,
            String phone,
            UserRole role,
            String department
    ) {
        Instant now = Instant.now();
        return new User(
                id,
                email,
                passwordHash,
                firstName,
                lastName,
                phone,
                role,
                UserStatus.ACTIVE,
                department,
                now,
                now
        );
    }

    /**
     * Reconstitutes an existing User aggregate from persistence.
     */
    public static User reconstitute(
            UserId id,
            EmailAddress email,
            String passwordHash,
            String firstName,
            String lastName,
            String phone,
            UserRole role,
            UserStatus status,
            String department,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new User(
                id,
                email,
                passwordHash,
                firstName,
                lastName,
                phone,
                role,
                status,
                department,
                createdAt,
                updatedAt
        );
    }

    // Business Methods

    public void updateProfile(String firstName, String lastName, String phone, String department) {
        this.firstName = validateNotBlank(firstName, "First name cannot be blank");
        this.lastName = validateNotBlank(lastName, "Last name cannot be blank");
        this.phone = phone != null ? phone.trim() : null;
        this.department = department != null ? department.trim() : null;
        this.updatedAt = Instant.now();
    }

    public void changePassword(String newPasswordHash) {
        this.passwordHash = validateNotBlank(newPasswordHash, "Password hash cannot be blank");
        this.updatedAt = Instant.now();
    }

    public void changeRole(UserRole newRole) {
        this.role = Objects.requireNonNull(newRole, "User role cannot be null");
        this.updatedAt = Instant.now();
    }

    public void activate() {
        if (this.status == UserStatus.ACTIVE) {
            return;
        }
        this.status = UserStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        if (this.status == UserStatus.INACTIVE) {
            return;
        }
        this.status = UserStatus.INACTIVE;
        this.updatedAt = Instant.now();
    }

    public void suspend() {
        if (this.status == UserStatus.SUSPENDED) {
            return;
        }
        this.status = UserStatus.SUSPENDED;
        this.updatedAt = Instant.now();
    }

    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Getters

    public UserId getId() {
        return id;
    }

    public EmailAddress getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public UserRole getRole() {
        return role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public String getDepartment() {
        return department;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    private static String validateNotBlank(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidUserDataException(message);
        }
        return value.trim();
    }
}
