package com.his.medcenter.auth.domain.exception;

import com.his.medcenter.common.domain.exception.DomainValidationException;

public class InvalidUserDataException extends DomainValidationException {

    public InvalidUserDataException(String message) {
        super(message);
    }
}
