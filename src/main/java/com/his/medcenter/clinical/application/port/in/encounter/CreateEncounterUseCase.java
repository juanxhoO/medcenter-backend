package com.his.medcenter.clinical.application.port.in;

import com.his.medcenter.clinical.application.dto.CreateEncounterCommand;
import com.his.medcenter.clinical.application.dto.EncounterResponseDto;

public interface CreateEncounterUseCase {

    EncounterResponseDto createEncounter(CreateEncounterCommand command);
}

