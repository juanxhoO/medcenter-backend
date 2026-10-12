package com.his.medcenter.auth.infrastructure.rest.dto;

import com.his.medcenter.user.domain.model.UserRole;

import java.util.UUID;

/**
 * Response DTO returned after successful authentication, containing
 * the access/refresh token pair and basic user identity.
 */
public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        UUID userId,
        String email,
        UserRole role) {
    public AuthResponse(String accessToken, String refreshToken, long expiresIn, UUID userId, String email,
            UserRole role) {
        this(accessToken, refreshToken, "Bearer", expiresIn, userId, email, role);
    }
}
