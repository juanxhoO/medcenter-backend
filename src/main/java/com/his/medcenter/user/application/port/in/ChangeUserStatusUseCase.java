package com.his.medcenter.user.application.port.in;

import com.his.medcenter.user.application.dto.UserResponseDto;
import com.his.medcenter.user.domain.model.UserId;

public interface ChangeUserStatusUseCase {
    UserResponseDto activateUser(UserId id);
    UserResponseDto deactivateUser(UserId id);
    UserResponseDto suspendUser(UserId id);
}
