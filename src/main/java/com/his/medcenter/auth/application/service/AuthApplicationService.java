package com.his.medcenter.auth.application.service;

import com.his.medcenter.auth.application.dto.LoginCommand;
import com.his.medcenter.auth.application.dto.RegisterCommand;
import com.his.medcenter.auth.application.port.in.LoginUseCase;
import com.his.medcenter.auth.application.port.in.RegisterUseCase;
import com.his.medcenter.user.application.dto.UserResponseDto;
import com.his.medcenter.user.domain.exception.EmailAlreadyInUseException;
import com.his.medcenter.user.domain.model.EmailAddress;
import com.his.medcenter.user.domain.model.User;
import com.his.medcenter.user.domain.model.UserId;
import com.his.medcenter.user.domain.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;

/**
 * Application service orchestrating authentication and registration use cases.
 */
@Service
@Transactional
public class AuthApplicationService implements
        RegisterUseCase,
        LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthApplicationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = Objects.requireNonNull(userRepository, "UserRepository cannot be null");
        this.passwordEncoder = Objects.requireNonNull(passwordEncoder, "PasswordEncoder cannot be null");
    }

    @Override
    public UserResponseDto register(RegisterCommand command) {
        EmailAddress email = EmailAddress.of(command.email());

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyInUseException(email);
        }

        String encodedPassword = passwordEncoder.encode(command.password());
        UserId newId = UserId.newUnique();

        User user = User.register(
                newId,
                email,
                encodedPassword,
                command.firstName(),
                command.lastName(),
                command.phone(),
                command.role(),
                null);

        User savedUser = userRepository.save(user);
        return UserResponseDto.fromDomain(savedUser);
    }

    @Override
    public UserResponseDto login(LoginCommand command) {

        // TODO Auto-generated method stub

        throw new UnsupportedOperationException("Unimplemented method 'login'");
    }
}
