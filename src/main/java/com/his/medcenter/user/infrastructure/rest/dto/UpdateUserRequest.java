package com.his.medcenter.user.infrastructure.rest.dto;

import com.his.medcenter.user.application.dto.UpdateUserProfileCommand;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(
        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        String phone,

        String department
) {
    public UpdateUserProfileCommand toCommand() {
        return new UpdateUserProfileCommand(
                firstName,
                lastName,
                phone,
                department
        );
    }
}
