package com.his.medcenter.auth.application.service;

import com.his.medcenter.auth.application.dto.LoginCommand;
import com.his.medcenter.auth.application.dto.RefreshTokenCommand;
import com.his.medcenter.auth.application.dto.RegisterCommand;
import com.his.medcenter.auth.application.dto.AuthResponseDto;
import com.his.medcenter.auth.application.dto.ForgotPassCommand;
import com.his.medcenter.auth.application.dto.ResetPasswordCommand;
import com.his.medcenter.auth.application.port.in.LoginUseCase;
import com.his.medcenter.auth.application.port.in.LogoutUseCase;
import com.his.medcenter.auth.application.port.in.RefreshTokenUseCase;
import com.his.medcenter.auth.application.port.in.RegisterUseCase;
import com.his.medcenter.auth.application.port.in.ForgotPasswordUseCase;
import com.his.medcenter.auth.application.port.in.ResetPasswordUseCase;
import com.his.medcenter.auth.infrastructure.rest.dto.AuthResponse;
import com.his.medcenter.common.security.JwtConfigProperties;
import com.his.medcenter.common.security.JwtService;
import com.his.medcenter.user.application.dto.UserResponseDto;
import com.his.medcenter.user.domain.exception.EmailAlreadyInUseException;
import com.his.medcenter.user.domain.model.EmailAddress;
import com.his.medcenter.user.domain.model.User;
import com.his.medcenter.user.domain.model.UserId;
import com.his.medcenter.user.domain.model.UserStatus;
import com.his.medcenter.user.domain.repository.UserRepository;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

/**
 * Application service orchestrating authentication and registration use cases.
 */
@Service
@Transactional
public class AuthApplicationService implements
        RegisterUseCase,
        LoginUseCase,
        LogoutUseCase,
        ForgotPasswordUseCase,
        RefreshTokenUseCase,
        ResetPasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtConfigProperties jwtConfigProperties;

    public AuthApplicationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            JwtConfigProperties jwtConfigProperties) {
        this.userRepository = Objects.requireNonNull(userRepository, "UserRepository cannot be null");
        this.passwordEncoder = Objects.requireNonNull(passwordEncoder, "PasswordEncoder cannot be null");
        this.jwtService = Objects.requireNonNull(jwtService, "JwtService cannot be null");
        this.jwtConfigProperties = Objects.requireNonNull(jwtConfigProperties, "JwtConfigProperties cannot be null");
    }

    @Override
    public UserResponseDto register(RegisterCommand command) {
        EmailAddress email = EmailAddress.of(command.email());

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyInUseException(email);
        }

        String encodedPassword = passwordEncoder.encode(command.password());
        UserId newId = UserId.newUnique();

        User user = User.register(
                newId,
                email,
                encodedPassword,
                command.firstName(),
                command.lastName(),
                command.phone(),
                command.role(),
                null);

        User savedUser = userRepository.save(user);
        return UserResponseDto.fromDomain(savedUser);
    }

    @Override
    public AuthResponseDto login(LoginCommand command) {

        User user = userRepository.findByEmail(EmailAddress.of(command.email())).orElse(null);

        if (user == null) {
            throw new BadCredentialsException("User not found");
        }

        if (!passwordEncoder.matches(command.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid password");
        }

        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new DisabledException("User account is disabled");
        }

        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new LockedException("User account is locked");
        }

        if (!user.isActive()) {
            throw new DisabledException("User account is not active");
        }

        String accessToken = jwtService.generateAccessToken(
                user.getId().value(),
                user.getEmail().value(),
                user.getRole().name());

        String refreshToken = jwtService.generateRefreshToken(user.getId().value());

        return new AuthResponseDto(
                accessToken,
                refreshToken,
                jwtConfigProperties.accessTokenExpirationMs() / 1000);
    }

    @Override
    public AuthResponse forgotPassword(ForgotPassCommand command) {
        EmailAddress email = EmailAddress.of(command.email());
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {

            throw new BadCredentialsException("User not found");

        }

        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new DisabledException("User account is disabled");
        }

        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new LockedException("User account is locked");
        }

        if (!user.isActive()) {
            throw new DisabledException("User account is not active");
        }

        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UserResponseDto resetPassword(ResetPasswordCommand command) {

        // TODO Auto-generated method stub

        throw new UnsupportedOperationException("Unimplemented method 'resetPassword'");
    }

    @Override
    public AuthResponseDto logout(String userId) {

        User user = userRepository.findById(UserId.of(UUID.fromString(userId)))
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new DisabledException("User account is disabled");
        }

        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new LockedException("User account is locked");
        }

        if (!user.isActive()) {
            throw new DisabledException("User account is not active");
        }
        return null;
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenCommand command) {
        if (command == null || command.refreshToken() == null || command.refreshToken().isBlank()) {
            throw new BadCredentialsException("Refresh token is required");
        }

        String refreshToken = command.refreshToken();

        if (!jwtService.isRefreshTokenValid(refreshToken)) {
            throw new BadCredentialsException("Invalid or expired refresh token");
        }

        String userIdStr = jwtService.extractSubject(refreshToken);
        UUID userId;
        try {
            userId = UUID.fromString(userIdStr);
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException("Invalid token subject");
        }

        User user = userRepository.findById(UserId.of(userId))
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new DisabledException("User account is disabled");
        }

        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new LockedException("User account is locked");
        }

        if (!user.isActive()) {
            throw new DisabledException("User account is not active");
        }

        String newAccessToken = jwtService.generateAccessToken(
                user.getId().value(),
                user.getEmail().value(),
                user.getRole().name());
        String newRefreshToken = jwtService.generateRefreshToken(user.getId().value());

        return new AuthResponse(
                newAccessToken,
                newRefreshToken,
                jwtConfigProperties.accessTokenExpirationMs() / 1000,
                user.getId().value(),
                user.getEmail().value(),
                user.getRole());
    }

}
