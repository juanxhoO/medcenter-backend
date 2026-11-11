package com.his.medcenter.clinical.domain.exception;

import com.his.medcenter.common.domain.exception.BusinessConflictException;

public class EncounterIllegalStateException extends BusinessConflictException {

    public EncounterIllegalStateException(String message) {
        super(message);
    }
}

