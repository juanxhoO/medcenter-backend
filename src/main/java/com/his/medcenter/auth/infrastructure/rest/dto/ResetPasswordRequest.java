package com.his.medcenter.auth.infrastructure.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
                @NotBlank(message = "Token is required") String token,
                @NotBlank(message = "Password is required") String password,
                @NotBlank(message = "Confirm Password is required") String confirmPassword) {
}
