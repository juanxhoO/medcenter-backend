package com.his.medcenter.clinical.application.port.in;

import com.his.medcenter.clinical.application.dto.CreatePrescriptionCommand;
import com.his.medcenter.clinical.application.dto.PrescriptionResponseDto;

public interface CreatePrescriptionUseCase {

    PrescriptionResponseDto createPrescription(CreatePrescriptionCommand command);
}

