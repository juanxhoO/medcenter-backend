package com.his.medcenter.patient.domain.exception;

import com.his.medcenter.common.domain.exception.DomainValidationException;

public class InvalidPatientDataException extends DomainValidationException {

    public InvalidPatientDataException(String message) {
        super(message);
    }
}

