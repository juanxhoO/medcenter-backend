package com.his.medcenter.patient.application.port.in;

import com.his.medcenter.patient.application.dto.CreatePatientCommand;
import com.his.medcenter.patient.application.dto.PatientResponseDto;

public interface CreatePatientUseCase {

    PatientResponseDto createPatient(CreatePatientCommand command);
}

