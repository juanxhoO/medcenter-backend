package com.his.medcenter.clinical.application.port.in;

import com.his.medcenter.clinical.application.dto.CreateVitalSignsCommand;
import com.his.medcenter.clinical.application.dto.VitalSignsResponseDto;

public interface CreateVitalSignsUseCase {

    VitalSignsResponseDto createVitalSigns(CreateVitalSignsCommand command);
}

