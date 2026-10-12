package com.his.medcenter.common.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Type-safe configuration properties for JWT authentication.
 *
 * @param secret                     Base64-encoded secret key (minimum 512 bits for HS512)
 * @param accessTokenExpirationMs    Access token time-to-live in milliseconds
 * @param refreshTokenExpirationMs   Refresh token time-to-live in milliseconds
 */
@ConfigurationProperties(prefix = "app.security.jwt")
public record JwtConfigProperties(
        String secret,
        long accessTokenExpirationMs,
        long refreshTokenExpirationMs
) {
}
