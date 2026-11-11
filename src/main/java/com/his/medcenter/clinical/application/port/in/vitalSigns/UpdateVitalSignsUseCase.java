package com.his.medcenter.clinical.application.port.in;

import com.his.medcenter.clinical.application.dto.UpdateVitalSignsCommand;
import com.his.medcenter.clinical.application.dto.VitalSignsResponseDto;
import com.his.medcenter.clinical.domain.model.VitalSignsId;

public interface UpdateVitalSignsUseCase {

    VitalSignsResponseDto updateVitalSigns(VitalSignsId id, UpdateVitalSignsCommand command);
}

