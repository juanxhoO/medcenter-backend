package com.his.medcenter.auth.application.port.in;

import com.his.medcenter.auth.application.dto.RegisterCommand;
import com.his.medcenter.user.application.dto.UserResponseDto;

public interface RegisterUseCase {
    UserResponseDto register(RegisterCommand command);
}
