package com.his.medcenter.user.application.service;

import com.his.medcenter.user.application.dto.RegisterUserCommand;
import com.his.medcenter.user.application.dto.UpdateUserProfileCommand;
import com.his.medcenter.user.application.dto.UserResponseDto;
import com.his.medcenter.user.application.port.in.ChangeUserStatusUseCase;
import com.his.medcenter.user.application.port.in.GetUserUseCase;
import com.his.medcenter.user.application.port.in.RegisterUserUseCase;
import com.his.medcenter.user.application.port.in.UpdateUserUseCase;
import com.his.medcenter.user.domain.exception.EmailAlreadyInUseException;
import com.his.medcenter.user.domain.exception.UserNotFoundException;
import com.his.medcenter.user.domain.model.EmailAddress;
import com.his.medcenter.user.domain.model.User;
import com.his.medcenter.user.domain.model.UserId;
import com.his.medcenter.user.domain.model.UserRole;
import com.his.medcenter.user.domain.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * Application service orchestrating user-related use cases and domain logic.
 */
@Service
@Transactional
public class UserApplicationService implements
        RegisterUserUseCase,
        GetUserUseCase,
        UpdateUserUseCase,
        ChangeUserStatusUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserApplicationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = Objects.requireNonNull(userRepository, "UserRepository cannot be null");
        this.passwordEncoder = Objects.requireNonNull(passwordEncoder, "PasswordEncoder cannot be null");
    }

    @Override
    public UserResponseDto registerUser(RegisterUserCommand command) {
        EmailAddress email = EmailAddress.of(command.email());

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyInUseException(email);
        }

        String encodedPassword = passwordEncoder.encode(command.rawPassword());
        UserId newId = UserId.newUnique();

        User user = User.register(
                newId,
                email,
                encodedPassword,
                command.firstName(),
                command.lastName(),
                command.phone(),
                command.role(),
                command.department()
        );

        User savedUser = userRepository.save(user);
        return UserResponseDto.fromDomain(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(UserId id) {
        return userRepository.findById(id)
                .map(UserResponseDto::fromDomain)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponseDto::fromDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role).stream()
                .map(UserResponseDto::fromDomain)
                .toList();
    }

    @Override
    public UserResponseDto updateProfile(UserId id, UpdateUserProfileCommand command) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.updateProfile(
                command.firstName(),
                command.lastName(),
                command.phone(),
                command.department()
        );

        User updatedUser = userRepository.save(user);
        return UserResponseDto.fromDomain(updatedUser);
    }

    @Override
    public UserResponseDto activateUser(UserId id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.activate();
        User updated = userRepository.save(user);
        return UserResponseDto.fromDomain(updated);
    }

    @Override
    public UserResponseDto deactivateUser(UserId id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.deactivate();
        User updated = userRepository.save(user);
        return UserResponseDto.fromDomain(updated);
    }

    @Override
    public UserResponseDto suspendUser(UserId id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.suspend();
        User updated = userRepository.save(user);
        return UserResponseDto.fromDomain(updated);
    }

    public void deleteUser(UserId id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }
}
