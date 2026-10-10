package com.his.medcenter.user.application.dto;

import com.his.medcenter.user.domain.model.UserRole;

public record RegisterUserCommand(
        String email,
        String rawPassword,
        String firstName,
        String lastName,
        String phone,
        UserRole role,
        String department
) {
}
