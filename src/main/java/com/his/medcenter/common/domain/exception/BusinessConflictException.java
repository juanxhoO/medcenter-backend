package com.his.medcenter.common.domain.exception;

public abstract class BusinessConflictException extends RuntimeException {

    protected BusinessConflictException(String message) {
        super(message);
    }
}
