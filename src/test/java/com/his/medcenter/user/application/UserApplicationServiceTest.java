package com.his.medcenter.user.application;

import com.his.medcenter.user.application.dto.RegisterUserCommand;
import com.his.medcenter.user.application.dto.UpdateUserProfileCommand;
import com.his.medcenter.user.application.dto.UserResponseDto;
import com.his.medcenter.user.application.service.UserApplicationService;
import com.his.medcenter.user.domain.exception.EmailAlreadyInUseException;
import com.his.medcenter.user.domain.exception.UserNotFoundException;
import com.his.medcenter.user.domain.model.EmailAddress;
import com.his.medcenter.user.domain.model.User;
import com.his.medcenter.user.domain.model.UserId;
import com.his.medcenter.user.domain.model.UserRole;
import com.his.medcenter.user.domain.model.UserStatus;
import com.his.medcenter.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserApplicationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserApplicationService userService;

    @BeforeEach
    void setUp() {
        userService = new UserApplicationService(userRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Should register user when email is not taken")
    void shouldRegisterUser() {
        RegisterUserCommand command = new RegisterUserCommand(
                "dr.house@medcenter.com",
                "secret123",
                "Gregory",
                "House",
                "+1555123456",
                UserRole.DOCTOR,
                "Diagnostics"
        );

        when(userRepository.existsByEmail(any(EmailAddress.class))).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("encodedSecret");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDto response = userService.registerUser(command);

        assertNotNull(response);
        assertEquals("dr.house@medcenter.com", response.email());
        assertEquals("Gregory House", response.fullName());
        assertEquals(UserRole.DOCTOR, response.role());
        assertEquals("Diagnostics", response.department());
        assertEquals(UserStatus.ACTIVE, response.status());

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw EmailAlreadyInUseException when registering existing email")
    void shouldThrowWhenEmailExists() {
        RegisterUserCommand command = new RegisterUserCommand(
                "taken@medcenter.com",
                "secret123",
                "Jane",
                "Doe",
                null,
                UserRole.NURSE,
                null
        );

        when(userRepository.existsByEmail(any(EmailAddress.class))).thenReturn(true);

        assertThrows(EmailAlreadyInUseException.class, () -> userService.registerUser(command));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should retrieve existing user by ID")
    void shouldGetUserById() {
        UserId id = UserId.newUnique();
        User user = User.register(
                id,
                EmailAddress.of("user@medcenter.com"),
                "hash",
                "First",
                "Last",
                null,
                UserRole.RECEPTIONIST,
                "FrontDesk"
        );

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UserResponseDto response = userService.getUserById(id);

        assertNotNull(response);
        assertEquals(id.value(), response.id());
        assertEquals("user@medcenter.com", response.email());
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user does not exist")
    void shouldThrowWhenUserNotFound() {
        UserId id = UserId.newUnique();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(id));
    }

    @Test
    @DisplayName("Should update user profile")
    void shouldUpdateProfile() {
        UserId id = UserId.newUnique();
        User user = User.register(
                id,
                EmailAddress.of("user@medcenter.com"),
                "hash",
                "OldFirst",
                "OldLast",
                null,
                UserRole.DOCTOR,
                "OldDept"
        );

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateUserProfileCommand command = new UpdateUserProfileCommand("NewFirst", "NewLast", "+111", "NewDept");
        UserResponseDto response = userService.updateProfile(id, command);

        assertEquals("NewFirst", response.firstName());
        assertEquals("NewLast", response.lastName());
        assertEquals("+111", response.phone());
        assertEquals("NewDept", response.department());
    }
}
