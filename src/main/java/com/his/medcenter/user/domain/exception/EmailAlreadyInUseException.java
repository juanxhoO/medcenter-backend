package com.his.medcenter.user.domain.exception;

import com.his.medcenter.common.domain.exception.BusinessConflictException;
import com.his.medcenter.user.domain.model.EmailAddress;

public class EmailAlreadyInUseException extends BusinessConflictException {

    public EmailAlreadyInUseException(EmailAddress email) {
        super("Email is already registered: " + email.value());
    }

    public EmailAlreadyInUseException(String message) {
        super(message);
    }
}
