package com.his.medcenter.clinical.domain.exception;

import com.his.medcenter.common.domain.exception.EntityNotFoundException;
import com.his.medcenter.clinical.domain.model.EncounterId;

public class EncounterNotFoundException extends EntityNotFoundException {

    public EncounterNotFoundException(EncounterId id) {
        super("Encounter not found with id: " + id.value());
    }

    public EncounterNotFoundException(String message) {
        super(message);
    }
}

