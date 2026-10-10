package com.his.medcenter.user.domain.exception;

import com.his.medcenter.common.domain.exception.EntityNotFoundException;
import com.his.medcenter.user.domain.model.UserId;

public class UserNotFoundException extends EntityNotFoundException {

    public UserNotFoundException(UserId id) {
        super("User not found with id: " + id.value());
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
