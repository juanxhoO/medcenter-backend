package com.his.medcenter.auth.application.dto;

import com.his.medcenter.user.domain.model.UserRole;

public record RegisterCommand(
                String email,
                String password,
                String firstName,
                String lastName,
                String phone,
                UserRole role) {
}
