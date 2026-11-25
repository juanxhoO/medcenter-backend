package com.his.medcenter.patient.application.port.in;

import com.his.medcenter.patient.application.dto.PatientResponseDto;
import com.his.medcenter.patient.domain.model.PatientId;

import java.util.List;
import java.util.UUID;

public interface GetPatientUseCase {

    PatientResponseDto getPatientById(PatientId id);

    PatientResponseDto getPatientByUserId(UUID userId);

    List<PatientResponseDto> getAllPatients();
}

