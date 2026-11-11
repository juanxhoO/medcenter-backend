package com.his.medcenter.clinical.domain.exception;

import com.his.medcenter.common.domain.exception.BusinessConflictException;

public class PrescriptionIllegalStateException extends BusinessConflictException {

    public PrescriptionIllegalStateException(String message) {
        super(message);
    }
}

