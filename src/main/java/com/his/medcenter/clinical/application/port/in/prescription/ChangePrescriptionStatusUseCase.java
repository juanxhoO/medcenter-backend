package com.his.medcenter.clinical.application.port.in;

import com.his.medcenter.clinical.application.dto.PrescriptionResponseDto;
import com.his.medcenter.clinical.domain.model.PrescriptionId;

public interface ChangePrescriptionStatusUseCase {

    PrescriptionResponseDto dispensePrescription(PrescriptionId id);

    PrescriptionResponseDto cancelPrescription(PrescriptionId id, String reason);
}

