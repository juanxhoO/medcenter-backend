package com.his.medcenter.user.application.port.in;

import com.his.medcenter.user.application.dto.RegisterUserCommand;
import com.his.medcenter.user.application.dto.UserResponseDto;

public interface RegisterUserUseCase {
    UserResponseDto registerUser(RegisterUserCommand command);
}
