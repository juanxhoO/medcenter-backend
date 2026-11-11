package com.his.medcenter.clinical.application.port.in;

import com.his.medcenter.clinical.application.dto.CompleteEncounterCommand;
import com.his.medcenter.clinical.application.dto.EncounterResponseDto;
import com.his.medcenter.clinical.domain.model.EncounterId;

public interface ChangeEncounterStatusUseCase {

    EncounterResponseDto startEncounter(EncounterId id);

    EncounterResponseDto completeEncounter(EncounterId id, CompleteEncounterCommand command);

    EncounterResponseDto cancelEncounter(EncounterId id, String reason);
}

