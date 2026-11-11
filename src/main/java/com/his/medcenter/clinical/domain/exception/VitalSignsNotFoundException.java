package com.his.medcenter.clinical.domain.exception;
import com.his.medcenter.common.domain.exception.EntityNotFoundException;
import com.his.medcenter.clinical.domain.model.VitalSignsId;

public class VitalSignsNotFoundException extends EntityNotFoundException {

    public VitalSignsNotFoundException(VitalSignsId id) {
        super("Vital Signs not found with id: " + id.value());
    }

    public VitalSignsNotFoundException(String message) {
        super(message);
    }
}

