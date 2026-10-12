package com.his.medcenter.user.infrastructure.security;

import com.his.medcenter.user.domain.model.EmailAddress;
import com.his.medcenter.user.domain.model.User;
import com.his.medcenter.user.domain.model.UserId;
import com.his.medcenter.user.domain.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Implementation of Spring Security's {@link UserDetailsService} that loads users
 * from the domain repository.
 *
 * <p>Supports lookup by both UUID (used by JwtAuthenticationFilter for token-based auth)
 * and email (used by AuthenticationManager for login).</p>
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads user by UUID string (primary, for JWT filter) or by email (fallback, for login).
     */
    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user = tryLoadByUUID(identifier);

        if (user == null) {
            user = tryLoadByEmail(identifier);
        }

        if (user == null) {
            throw new UsernameNotFoundException("User not found with identifier: " + identifier);
        }

        return new UserPrincipal(user);
    }

    private User tryLoadByUUID(String identifier) {
        try {
            UUID uuid = UUID.fromString(identifier);
            return userRepository.findById(UserId.of(uuid)).orElse(null);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private User tryLoadByEmail(String identifier) {
        try {
            EmailAddress email = EmailAddress.of(identifier);
            return userRepository.findByEmail(email).orElse(null);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
