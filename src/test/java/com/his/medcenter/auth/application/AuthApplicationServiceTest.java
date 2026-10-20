package com.his.medcenter.auth.application;

import com.his.medcenter.auth.application.dto.RefreshTokenCommand;
import com.his.medcenter.auth.application.service.AuthApplicationService;
import com.his.medcenter.auth.infrastructure.rest.dto.AuthResponse;
import com.his.medcenter.common.security.JwtConfigProperties;
import com.his.medcenter.common.security.JwtService;
import com.his.medcenter.user.domain.model.EmailAddress;
import com.his.medcenter.user.domain.model.User;
import com.his.medcenter.user.domain.model.UserId;
import com.his.medcenter.user.domain.model.UserRole;
import com.his.medcenter.user.domain.model.UserStatus;
import com.his.medcenter.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthApplicationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private JwtConfigProperties jwtConfigProperties;

    private AuthApplicationService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthApplicationService(
                userRepository,
                passwordEncoder,
                jwtService,
                jwtConfigProperties
        );
    }

    @Test
    @DisplayName("Should successfully refresh token when valid")
    void shouldRefreshTokenSuccessfully() {
        String oldRefreshToken = "valid-refresh-token";
        UUID uuid = UUID.randomUUID();
        UserId userId = UserId.of(uuid);

        User user = User.reconstitute(
                userId,
                EmailAddress.of("doctor@medcenter.com"),
                "hash",
                "Gregory",
                "House",
                "+123456789",
                UserRole.DOCTOR,
                UserStatus.ACTIVE,
                "Diagnostics",
                Instant.now(),
                Instant.now()
        );

        when(jwtService.isRefreshTokenValid(oldRefreshToken)).thenReturn(true);
        when(jwtService.extractSubject(oldRefreshToken)).thenReturn(uuid.toString());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jwtConfigProperties.accessTokenExpirationMs()).thenReturn(900_000L);
        when(jwtService.generateAccessToken(uuid, "doctor@medcenter.com", UserRole.DOCTOR.name()))
                .thenReturn("new-access-token");
        when(jwtService.generateRefreshToken(uuid))
                .thenReturn("new-refresh-token");

        RefreshTokenCommand command = new RefreshTokenCommand(oldRefreshToken);
        AuthResponse response = authService.refreshToken(command);

        assertNotNull(response);
        assertEquals("new-access-token", response.accessToken());
        assertEquals("new-refresh-token", response.refreshToken());
        assertEquals(900L, response.expiresIn());
        assertEquals(uuid, response.userId());
        assertEquals("doctor@medcenter.com", response.email());
        assertEquals(UserRole.DOCTOR, response.role());
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when refresh token is invalid or expired")
    void shouldThrowWhenRefreshTokenInvalid() {
        String invalidToken = "invalid-token";
        when(jwtService.isRefreshTokenValid(invalidToken)).thenReturn(false);

        RefreshTokenCommand command = new RefreshTokenCommand(invalidToken);

        assertThrows(BadCredentialsException.class, () -> authService.refreshToken(command));
        verify(userRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when user is not found")
    void shouldThrowWhenUserNotFound() {
        String validToken = "valid-token";
        UUID uuid = UUID.randomUUID();

        when(jwtService.isRefreshTokenValid(validToken)).thenReturn(true);
        when(jwtService.extractSubject(validToken)).thenReturn(uuid.toString());
        when(userRepository.findById(UserId.of(uuid))).thenReturn(Optional.empty());

        RefreshTokenCommand command = new RefreshTokenCommand(validToken);

        assertThrows(BadCredentialsException.class, () -> authService.refreshToken(command));
    }

    @Test
    @DisplayName("Should throw DisabledException when user account is inactive")
    void shouldThrowWhenUserIsInactive() {
        String validToken = "valid-token";
        UUID uuid = UUID.randomUUID();
        UserId userId = UserId.of(uuid);

        User user = User.reconstitute(
                userId,
                EmailAddress.of("user@medcenter.com"),
                "hash",
                "First",
                "Last",
                null,
                UserRole.PATIENT,
                UserStatus.INACTIVE,
                null,
                Instant.now(),
                Instant.now()
        );

        when(jwtService.isRefreshTokenValid(validToken)).thenReturn(true);
        when(jwtService.extractSubject(validToken)).thenReturn(uuid.toString());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        RefreshTokenCommand command = new RefreshTokenCommand(validToken);

        assertThrows(DisabledException.class, () -> authService.refreshToken(command));
    }

    @Test
    @DisplayName("Should throw LockedException when user account is suspended")
    void shouldThrowWhenUserIsSuspended() {
        String validToken = "valid-token";
        UUID uuid = UUID.randomUUID();
        UserId userId = UserId.of(uuid);

        User user = User.reconstitute(
                userId,
                EmailAddress.of("user@medcenter.com"),
                "hash",
                "First",
                "Last",
                null,
                UserRole.PATIENT,
                UserStatus.SUSPENDED,
                null,
                Instant.now(),
                Instant.now()
        );

        when(jwtService.isRefreshTokenValid(validToken)).thenReturn(true);
        when(jwtService.extractSubject(validToken)).thenReturn(uuid.toString());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        RefreshTokenCommand command = new RefreshTokenCommand(validToken);

        assertThrows(LockedException.class, () -> authService.refreshToken(command));
    }
}
