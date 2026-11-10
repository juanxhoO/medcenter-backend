package com.his.medcenter.clinical.application.port.in;

import com.his.medcenter.clinical.application.dto.EncounterResponseDto;
import com.his.medcenter.clinical.domain.model.EncounterId;
import com.his.medcenter.clinical.domain.model.EncounterStatus;

import java.util.List;
import java.util.UUID;

public interface GetEncounterUseCase {

    EncounterResponseDto getEncounterById(EncounterId id);

    List<EncounterResponseDto> getAllEncounters();

    List<EncounterResponseDto> getEncountersByPatient(UUID patientId);

    List<EncounterResponseDto> getEncountersByPractitioner(UUID practitionerId);

    List<EncounterResponseDto> getEncountersByStatus(EncounterStatus status);
}

