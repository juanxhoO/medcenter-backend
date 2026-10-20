package com.his.medcenter.auth.application.port.in;

import com.his.medcenter.auth.application.dto.AuthResponseDto;

public interface LogoutUseCase {
    AuthResponseDto logout(String userId);
}
