package com.his.medcenter.user.infrastructure.rest.dto;

import com.his.medcenter.user.domain.model.UserStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeStatusRequest(
        @NotNull(message = "Status is required")
        UserStatus status
) {
}
