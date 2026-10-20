package com.his.medcenter.auth.infrastructure.rest;

import com.his.medcenter.auth.application.dto.AuthResponseDto;
import com.his.medcenter.auth.application.port.in.ForgotPasswordUseCase;
import com.his.medcenter.auth.application.port.in.LogoutUseCase;
import com.his.medcenter.auth.application.port.in.RefreshTokenUseCase;
import com.his.medcenter.auth.infrastructure.rest.dto.AuthResponse;
import com.his.medcenter.auth.infrastructure.rest.dto.ForgotPasswordRequest;
import com.his.medcenter.auth.infrastructure.rest.dto.LoginRequest;
import com.his.medcenter.auth.infrastructure.rest.dto.RefreshTokenRequest;
import com.his.medcenter.auth.infrastructure.rest.dto.RegisterRequest;
import com.his.medcenter.auth.infrastructure.rest.dto.ResetPasswordRequest;
import com.his.medcenter.common.security.JwtConfigProperties;
import com.his.medcenter.common.security.JwtService;
import com.his.medcenter.user.domain.model.UserRole;
import com.his.medcenter.user.infrastructure.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

/**
 * REST controller handling authentication operations:
 * login (issuing tokens) and token refresh.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

        private final AuthenticationManager authenticationManager;
        private final JwtService jwtService;
        private final JwtConfigProperties jwtConfigProperties;
        private final UserDetailsService userDetailsService;
        private final RefreshTokenUseCase refreshTokenUseCase;
        private final ForgotPasswordUseCase forgotPasswordUseCase;
        private final LogoutUseCase logoutUseCase;

        public AuthController(
                        AuthenticationManager authenticationManager,
                        JwtService jwtService,
                        JwtConfigProperties jwtConfigProperties,
                        UserDetailsService userDetailsService,
                        RefreshTokenUseCase refreshTokenUseCase,
                        ForgotPasswordUseCase forgotPasswordUseCase,
                        LogoutUseCase logoutUseCase) {
                this.authenticationManager = authenticationManager;
                this.jwtService = jwtService;
                this.jwtConfigProperties = jwtConfigProperties;
                this.userDetailsService = userDetailsService;
                this.refreshTokenUseCase = refreshTokenUseCase;
                this.forgotPasswordUseCase = forgotPasswordUseCase;
                this.logoutUseCase = logoutUseCase;
        }

        /**
         * Authenticates a user with email/password and returns an access + refresh
         * token pair.
         */
        @PostMapping("/login")
        public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

                UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
                UUID userId = principal.getUserId();
                String email = principal.getEmail();
                String roleName = principal.getAuthorities().iterator().next().getAuthority();
                UserRole role = UserRole.valueOf(roleName.replace("ROLE_", ""));

                String accessToken = jwtService.generateAccessToken(userId, email, role.name());
                String refreshToken = jwtService.generateRefreshToken(userId);

                return ResponseEntity.ok(new AuthResponse(
                                accessToken,
                                refreshToken,
                                jwtConfigProperties.accessTokenExpirationMs() / 1000,
                                userId,
                                email,
                                role));
        }

        @PostMapping("/reset-password")
        public ResponseEntity<AuthResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
                return ResponseEntity.ok(new AuthResponse(
                                null,
                                null,
                                0,
                                null,
                                null,
                                null));
        }

        @PostMapping("/forgot-password")
        public ResponseEntity<AuthResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
                AuthResponse response = forgotPasswordUseCase.forgotPassword(request.toCommand());
                System.out.println(response);
                return ResponseEntity.ok(response);
        }

        @PostMapping("/logout")
        public ResponseEntity<Void> logout(@AuthenticationPrincipal UserPrincipal userPrincipal) {
                logoutUseCase.logout(userPrincipal.getUserId().toString());
                return ResponseEntity.noContent().build();
        }

        @PostMapping("/register")
        public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
                return ResponseEntity.ok(new AuthResponse(
                                null,
                                null,
                                0,
                                null,
                                null,
                                null));
        }

        /**
         * Issues a new access + refresh token pair using a valid refresh token.
         * The old refresh token is implicitly invalidated by issuing a new one.
         */
        @PostMapping("/refresh")
        public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
                AuthResponse response = refreshTokenUseCase.refreshToken(request.toCommand());
                return ResponseEntity.ok(response);
        }
}
