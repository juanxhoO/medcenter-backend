package com.his.medcenter.clinical.application.port.in;

import com.his.medcenter.clinical.application.dto.EncounterResponseDto;
import com.his.medcenter.clinical.application.dto.UpdateEncounterCommand;
import com.his.medcenter.clinical.domain.model.EncounterId;

public interface UpdateEncounterUseCase {

    EncounterResponseDto updateEncounter(EncounterId id, UpdateEncounterCommand command);
}

