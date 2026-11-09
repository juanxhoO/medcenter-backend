package com.his.medcenter.clinical.domain.exception;

import com.his.medcenter.common.domain.exception.DomainValidationException;

public class InvalidEncounterDataException extends DomainValidationException {

    public InvalidEncounterDataException(String message) {
        super(message);
    }
}

