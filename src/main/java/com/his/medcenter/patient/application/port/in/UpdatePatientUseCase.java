package com.his.medcenter.patient.application.port.in;

import com.his.medcenter.patient.application.dto.*;
import com.his.medcenter.patient.domain.model.PatientId;

public interface UpdatePatientUseCase {

    PatientResponseDto updateDemographics(PatientId id, UpdatePatientDemographicsCommand command);

    PatientResponseDto updateClinicalInfo(PatientId id, UpdatePatientClinicalInfoCommand command);

    PatientResponseDto updateEmergencyContact(PatientId id, UpdateEmergencyContactCommand command);

    PatientResponseDto updateInsurance(PatientId id, UpdateInsuranceCommand command);
}

