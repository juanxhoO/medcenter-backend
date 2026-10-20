package com.his.medcenter.auth.infrastructure.rest;

import com.his.medcenter.auth.application.dto.RefreshTokenCommand;
import com.his.medcenter.auth.application.port.in.ForgotPasswordUseCase;
import com.his.medcenter.auth.application.port.in.LogoutUseCase;
import com.his.medcenter.auth.application.port.in.RefreshTokenUseCase;
import com.his.medcenter.auth.infrastructure.rest.dto.AuthResponse;
import com.his.medcenter.auth.infrastructure.rest.dto.RefreshTokenRequest;
import com.his.medcenter.common.security.JwtConfigProperties;
import com.his.medcenter.common.security.JwtService;
import com.his.medcenter.user.domain.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private JwtConfigProperties jwtConfigProperties;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private RefreshTokenUseCase refreshTokenUseCase;

    @Mock
    private ForgotPasswordUseCase forgotPasswordUseCase;

    @Mock
    private LogoutUseCase logoutUseCase;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        authController = new AuthController(
                authenticationManager,
                jwtService,
                jwtConfigProperties,
                userDetailsService,
                refreshTokenUseCase,
                forgotPasswordUseCase,
                logoutUseCase);
    }

    @Test
    @DisplayName("POST /refresh - Should delegate to RefreshTokenUseCase and return 200 OK")
    void shouldRefreshTokenSuccessfully() {
        UUID userId = UUID.randomUUID();
        RefreshTokenRequest request = new RefreshTokenRequest("dummy-refresh-token");
        AuthResponse expectedResponse = new AuthResponse(
                "new-access-token",
                "new-refresh-token",
                900L,
                userId,
                "user@medcenter.com",
                UserRole.DOCTOR);

        when(refreshTokenUseCase.refreshToken(new RefreshTokenCommand("dummy-refresh-token")))
                .thenReturn(expectedResponse);

        ResponseEntity<AuthResponse> responseEntity = authController.refreshToken(request);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedResponse, responseEntity.getBody());
        verify(refreshTokenUseCase).refreshToken(new RefreshTokenCommand("dummy-refresh-token"));
    }
}
