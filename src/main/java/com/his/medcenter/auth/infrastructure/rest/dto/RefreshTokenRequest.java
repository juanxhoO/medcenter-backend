package com.his.medcenter.auth.infrastructure.rest.dto;

import com.his.medcenter.auth.application.dto.RefreshTokenCommand;
import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
                @NotBlank(message = "Refresh token is required") String refreshToken) {

    public RefreshTokenCommand toCommand() {
        return new RefreshTokenCommand(refreshToken);
    }
}
