package com.his.medcenter.user.domain;

import com.his.medcenter.user.domain.exception.InvalidUserDataException;
import com.his.medcenter.user.domain.model.EmailAddress;
import com.his.medcenter.user.domain.model.User;
import com.his.medcenter.user.domain.model.UserId;
import com.his.medcenter.user.domain.model.UserRole;
import com.his.medcenter.user.domain.model.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("Should successfully register a new user in ACTIVE status")
    void shouldRegisterNewUser() {
        UserId id = UserId.newUnique();
        EmailAddress email = EmailAddress.of("dr.smith@medcenter.com");

        User user = User.register(
                id,
                email,
                "hashedSecret123",
                "John",
                "Smith",
                "+1234567890",
                UserRole.DOCTOR,
                "Cardiology"
        );

        assertNotNull(user);
        assertEquals(id, user.getId());
        assertEquals("dr.smith@medcenter.com", user.getEmail().value());
        assertEquals(UserRole.DOCTOR, user.getRole());
        assertEquals(UserStatus.ACTIVE, user.getStatus());
        assertEquals("Cardiology", user.getDepartment());
        assertEquals("John Smith", user.getFullName());
        assertTrue(user.isActive());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
    }

    @Test
    @DisplayName("Should reject blank first name")
    void shouldRejectBlankFirstName() {
        UserId id = UserId.newUnique();
        EmailAddress email = EmailAddress.of("nurse.jane@medcenter.com");

        assertThrows(InvalidUserDataException.class, () ->
                User.register(
                        id,
                        email,
                        "hashedSecret123",
                        "   ",
                        "Doe",
                        null,
                        UserRole.NURSE,
                        "Pediatrics"
                )
        );
    }

    @Test
    @DisplayName("Should properly update profile")
    void shouldUpdateProfile() {
        User user = User.register(
                UserId.newUnique(),
                EmailAddress.of("admin@medcenter.com"),
                "hash",
                "Admin",
                "User",
                null,
                UserRole.ADMIN,
                "IT"
        );

        user.updateProfile("Super", "Admin", "+9999999", "Executive");

        assertEquals("Super", user.getFirstName());
        assertEquals("Admin", user.getLastName());
        assertEquals("+9999999", user.getPhone());
        assertEquals("Executive", user.getDepartment());
    }

    @Test
    @DisplayName("Should transition status between ACTIVE, INACTIVE, and SUSPENDED")
    void shouldTransitionStatus() {
        User user = User.register(
                UserId.newUnique(),
                EmailAddress.of("test@medcenter.com"),
                "hash",
                "First",
                "Last",
                null,
                UserRole.PATIENT,
                null
        );

        assertEquals(UserStatus.ACTIVE, user.getStatus());

        user.deactivate();
        assertEquals(UserStatus.INACTIVE, user.getStatus());
        assertFalse(user.isActive());

        user.activate();
        assertEquals(UserStatus.ACTIVE, user.getStatus());
        assertTrue(user.isActive());

        user.suspend();
        assertEquals(UserStatus.SUSPENDED, user.getStatus());
    }
}
