package com.his.medcenter.user.infrastructure.security;

import com.his.medcenter.user.domain.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Adapter bridging the domain {@link User} aggregate to Spring Security's {@link UserDetails} contract.
 * Carries the user's UUID as the principal username for JWT subject mapping.
 */
public class UserPrincipal implements UserDetails {

    private final UUID userId;
    private final String email;
    private final String passwordHash;
    private final boolean active;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(User user) {
        this.userId = user.getId().value();
        this.email = user.getEmail().value();
        this.passwordHash = user.getPasswordHash();
        this.active = user.isActive();
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    public UUID getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    /**
     * Returns the user's UUID as the username. This matches the JWT subject claim
     * and is used by the JwtAuthenticationFilter for identity resolution.
     */
    @Override
    public String getUsername() {
        return userId.toString();
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
