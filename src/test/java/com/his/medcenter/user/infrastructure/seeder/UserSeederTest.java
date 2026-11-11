package com.his.medcenter.user.infrastructure.seeder;

import com.his.medcenter.user.domain.model.EmailAddress;
import com.his.medcenter.user.domain.model.User;
import com.his.medcenter.user.domain.model.UserRole;
import com.his.medcenter.user.domain.model.UserStatus;
import com.his.medcenter.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSeederTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserSeedingProperties properties;
    private UserSeeder userSeeder;

    @BeforeEach
    void setUp() {
        properties = new UserSeedingProperties(
                true,
                new UserSeedingProperties.AdminSeedProperties(
                        "admin@medcenter.com",
                        "Admin123!",
                        "System",
                        "Admin",
                        "+15550000001",
                        "Administration"
                )
        );
        userSeeder = new UserSeeder(userRepository, passwordEncoder, properties);
    }

    @Test
    @DisplayName("Should seed admin user when user does not exist in the database")
    void shouldSeedAdminUserWhenNotExists() {
        EmailAddress email = EmailAddress.of("admin@medcenter.com");
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode("Admin123!")).thenReturn("hashed-admin-password");

        userSeeder.run();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertNotNull(savedUser.getId());
        assertEquals("admin@medcenter.com", savedUser.getEmail().value());
        assertEquals("hashed-admin-password", savedUser.getPasswordHash());
        assertEquals("System", savedUser.getFirstName());
        assertEquals("Admin", savedUser.getLastName());
        assertEquals("+15550000001", savedUser.getPhone());
        assertEquals(UserRole.ADMIN, savedUser.getRole());
        assertEquals(UserStatus.ACTIVE, savedUser.getStatus());
        assertEquals("Administration", savedUser.getDepartment());
    }

    @Test
    @DisplayName("Should skip seeding when admin user already exists")
    void shouldSkipSeedingWhenAdminUserAlreadyExists() {
        EmailAddress email = EmailAddress.of("admin@medcenter.com");
        when(userRepository.existsByEmail(email)).thenReturn(true);

        userSeeder.run();

        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }
}
