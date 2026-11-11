package com.his.medcenter.clinical.application.port.in;

import com.his.medcenter.clinical.application.dto.VitalSignsResponseDto;
import com.his.medcenter.clinical.domain.model.VitalSignsId;

import java.util.List;
import java.util.UUID;

public interface GetVitalSignsUseCase {

    VitalSignsResponseDto getVitalSignsById(VitalSignsId id);

    List<VitalSignsResponseDto> getAllVitalSigns();

    List<VitalSignsResponseDto> getVitalSignsByPatient(UUID patientId);

    List<VitalSignsResponseDto> getVitalSignsByPractitioner(UUID practitionerId);

    List<VitalSignsResponseDto> getVitalSignsByEncounter(UUID encounterId);
}