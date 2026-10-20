package com.his.medcenter.auth.application.port.in;

import com.his.medcenter.auth.application.dto.ResetPasswordCommand;
import com.his.medcenter.user.application.dto.UserResponseDto;

public interface ResetPasswordUseCase {
    UserResponseDto resetPassword(ResetPasswordCommand command);
}
