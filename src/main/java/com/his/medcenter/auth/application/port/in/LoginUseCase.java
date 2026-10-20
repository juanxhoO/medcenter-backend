package com.his.medcenter.auth.application.port.in;

import com.his.medcenter.auth.application.dto.AuthResponseDto;
import com.his.medcenter.auth.application.dto.LoginCommand;

public interface LoginUseCase {
    AuthResponseDto login(LoginCommand command);
}
