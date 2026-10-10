package com.his.medcenter.user.application.port.in;

import com.his.medcenter.user.application.dto.UserResponseDto;
import com.his.medcenter.user.domain.model.UserId;
import com.his.medcenter.user.domain.model.UserRole;

import java.util.List;

public interface GetUserUseCase {
    UserResponseDto getUserById(UserId id);
    List<UserResponseDto> getAllUsers();
    List<UserResponseDto> getUsersByRole(UserRole role);
}
