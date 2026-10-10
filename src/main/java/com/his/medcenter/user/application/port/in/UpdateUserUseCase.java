package com.his.medcenter.user.application.port.in;

import com.his.medcenter.user.application.dto.UpdateUserProfileCommand;
import com.his.medcenter.user.application.dto.UserResponseDto;
import com.his.medcenter.user.domain.model.UserId;

public interface UpdateUserUseCase {
    UserResponseDto updateProfile(UserId id, UpdateUserProfileCommand command);
}
