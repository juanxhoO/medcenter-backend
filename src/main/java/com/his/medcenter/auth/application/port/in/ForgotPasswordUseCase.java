package com.his.medcenter.auth.application.port.in;

import com.his.medcenter.auth.application.dto.ForgotPassCommand;
import com.his.medcenter.auth.infrastructure.rest.dto.AuthResponse;

public interface ForgotPasswordUseCase {
    AuthResponse forgotPassword(ForgotPassCommand command);
}
