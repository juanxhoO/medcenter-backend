package com.his.medcenter.user.application.dto;

public record UpdateUserProfileCommand(
        String firstName,
        String lastName,
        String phone,
        String department
) {
}
