package com.his.medcenter.auth.infrastructure.rest;

import com.his.medcenter.common.security.JwtConfigProperties;
import com.his.medcenter.common.security.JwtService;
import com.his.medcenter.user.domain.model.UserRole;
import com.his.medcenter.user.infrastructure.rest.dto.AuthResponse;
import com.his.medcenter.user.infrastructure.rest.dto.LoginRequest;
import com.his.medcenter.user.infrastructure.rest.dto.RefreshTokenRequest;
import com.his.medcenter.user.infrastructure.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            JwtConfigProperties jwtConfigProperties,
            UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.jwtConfigProperties = jwtConfigProperties;
        this.userDetailsService = userDetailsService;
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

    /**
     * Issues a new access + refresh token pair using a valid refresh token.
     * The old refresh token is implicitly invalidated by issuing a new one.
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();

        if (!jwtService.isRefreshTokenValid(refreshToken)) {
            throw new BadCredentialsException("Invalid or expired refresh token");
        }

        String userId = jwtService.extractSubject(refreshToken);
        UserPrincipal principal = (UserPrincipal) userDetailsService.loadUserByUsername(userId);

        if (!principal.isEnabled()) {
            throw new DisabledException("User account is disabled");
        }

        if (!principal.isAccountNonLocked()) {
            throw new LockedException("User account is locked");
        }

        String roleName = principal.getAuthorities().iterator().next().getAuthority();
        UserRole role = UserRole.valueOf(roleName.replace("ROLE_", ""));

        String newAccessToken = jwtService.generateAccessToken(principal.getUserId(), principal.getEmail(),
                role.name());
        String newRefreshToken = jwtService.generateRefreshToken(principal.getUserId());

        return ResponseEntity.ok(new AuthResponse(
                newAccessToken,
                newRefreshToken,
                jwtConfigProperties.accessTokenExpirationMs() / 1000,
                principal.getUserId(),
                principal.getEmail(),
                role));
    }
}
