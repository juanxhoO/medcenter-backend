package com.his.medcenter.user.infrastructure.rest.dto;

import com.his.medcenter.user.application.dto.RegisterUserCommand;
import com.his.medcenter.user.domain.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password,

        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        String phone,

        @NotNull(message = "User role is required")
        UserRole role,

        String department
) {
    public RegisterUserCommand toCommand() {
        return new RegisterUserCommand(
                email,
                password,
                firstName,
                lastName,
                phone,
                role,
                department
        );
    }
}
