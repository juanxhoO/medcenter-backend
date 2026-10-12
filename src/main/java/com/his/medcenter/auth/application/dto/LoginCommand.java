package com.his.medcenter.auth.application.dto;

public record LoginCommand(
        String email,
        String password) {
}
