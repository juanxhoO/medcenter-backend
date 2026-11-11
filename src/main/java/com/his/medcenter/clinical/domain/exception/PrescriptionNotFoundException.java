package com.his.medcenter.clinical.domain.exception;

import com.his.medcenter.common.domain.exception.EntityNotFoundException;
import com.his.medcenter.clinical.domain.model.PrescriptionId;

public class PrescriptionNotFoundException extends EntityNotFoundException {

    public PrescriptionNotFoundException(PrescriptionId id) {
        super("Prescription not found with id: " + id.value());
    }

    public PrescriptionNotFoundException(String message) {
        super(message);
    }
}

