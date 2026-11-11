package com.his.medcenter.auth.application.port.in;

import com.his.medcenter.auth.application.dto.RefreshTokenCommand;
import com.his.medcenter.auth.infrastructure.rest.dto.AuthResponse;

public interface RefreshTokenUseCase {
    AuthResponse refreshToken(RefreshTokenCommand command);
}
