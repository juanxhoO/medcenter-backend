package com.his.medcenter.patient.domain.exception;

import com.his.medcenter.common.domain.exception.EntityNotFoundException;
import com.his.medcenter.patient.domain.model.PatientId;

public class PatientNotFoundException extends EntityNotFoundException {

    public PatientNotFoundException(PatientId id) {
        super("Patient not found with id: " + id.value());
    }

    public PatientNotFoundException(String message) {
        super(message);
    }
}

