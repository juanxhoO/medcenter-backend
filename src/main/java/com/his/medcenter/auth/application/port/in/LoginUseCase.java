package com.his.medcenter.auth.application.port.in;

import com.his.medcenter.auth.application.dto.LoginCommand;
import com.his.medcenter.user.application.dto.UserResponseDto;

public interface LoginUseCase {
    UserResponseDto login(LoginCommand command);
}
