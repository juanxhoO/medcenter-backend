package com.his.medcenter.user.application.dto;

import com.his.medcenter.user.domain.model.User;
import com.his.medcenter.user.domain.model.UserRole;
import com.his.medcenter.user.domain.model.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String fullName,
        String phone,
        UserRole role,
        UserStatus status,
        String department,
        Instant createdAt,
        Instant updatedAt
) {
    public static UserResponseDto fromDomain(User user) {
        return new UserResponseDto(
                user.getId().value(),
                user.getEmail().value(),
                user.getFirstName(),
                user.getLastName(),
                user.getFullName(),
                user.getPhone(),
                user.getRole(),
                user.getStatus(),
                user.getDepartment(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
