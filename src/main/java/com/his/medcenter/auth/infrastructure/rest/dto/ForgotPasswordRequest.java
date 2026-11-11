package com.his.medcenter.auth.infrastructure.rest.dto;

import com.his.medcenter.auth.application.dto.ForgotPassCommand;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(
                @NotBlank(message = "Email is required") @Email(message = "Email must be valid") String email) {

        public ForgotPassCommand toCommand() {
                return new ForgotPassCommand(email);
        }
}
