package com.his.medcenter.clinical.domain.exception;

import com.his.medcenter.common.domain.exception.DomainValidationException;

public class InvalidPrescriptionDataException extends DomainValidationException {

    public InvalidPrescriptionDataException(String message) {
        super(message);
    }
}

