package com.his.medcenter.patient.domain.exception;

import com.his.medcenter.common.domain.exception.BusinessConflictException;

public class PatientAlreadyExistsException extends BusinessConflictException {

    public PatientAlreadyExistsException(String message) {
        super(message);
    }
}

