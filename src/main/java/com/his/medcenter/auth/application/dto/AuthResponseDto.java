package com.his.medcenter.auth.application.dto;

public record AuthResponseDto(
        String accessToken,
        String refreshToken,
        long accessTokenExpirationMs) {
    public static AuthResponseDto fromDomain(String accessToken, String refreshToken,
            long accessTokenExpirationMs) {
        return new AuthResponseDto(
                accessToken,
                refreshToken,
                accessTokenExpirationMs);
    }
}
