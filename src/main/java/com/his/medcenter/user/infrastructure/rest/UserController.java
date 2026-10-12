package com.his.medcenter.user.infrastructure.rest;

import com.his.medcenter.user.application.dto.UserResponseDto;
import com.his.medcenter.user.application.port.in.ChangeUserStatusUseCase;
import com.his.medcenter.user.application.port.in.GetUserUseCase;
import com.his.medcenter.user.application.port.in.RegisterUserUseCase;
import com.his.medcenter.user.application.port.in.UpdateUserUseCase;
import com.his.medcenter.user.application.service.UserApplicationService;
import com.his.medcenter.user.domain.model.UserId;
import com.his.medcenter.user.domain.model.UserRole;
import com.his.medcenter.user.infrastructure.rest.dto.ChangeStatusRequest;
import com.his.medcenter.user.infrastructure.rest.dto.CreateUserRequest;
import com.his.medcenter.user.infrastructure.rest.dto.UpdateUserRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final RegisterUserUseCase registerUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final ChangeUserStatusUseCase changeUserStatusUseCase;
    private final UserApplicationService userApplicationService;

    public UserController(
            RegisterUserUseCase registerUserUseCase,
            GetUserUseCase getUserUseCase,
            UpdateUserUseCase updateUserUseCase,
            ChangeUserStatusUseCase changeUserStatusUseCase,
            UserApplicationService userApplicationService) {
        this.registerUserUseCase = registerUserUseCase;
        this.getUserUseCase = getUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.changeUserStatusUseCase = changeUserStatusUseCase;
        this.userApplicationService = userApplicationService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponseDto created = registerUserUseCase.registerUser(request.toCommand());
        URI location = URI.create("/api/v1/users/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable UUID id) {
        UserResponseDto user = getUserUseCase.getUserById(UserId.of(id));
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getUsers(@RequestParam(required = false) UserRole role) {
        List<UserResponseDto> users = role != null
                ? getUserUseCase.getUsersByRole(role)
                : getUserUseCase.getAllUsers();
        System.out.println("Usuarios: " + users);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateProfile(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request) {
        UserResponseDto updated = updateUserUseCase.updateProfile(UserId.of(id), request.toCommand());
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponseDto> changeStatus(
            @PathVariable UUID id,
            @Valid @RequestBody ChangeStatusRequest request) {
        UserId userId = UserId.of(id);
        UserResponseDto updated = switch (request.status()) {
            case ACTIVE -> changeUserStatusUseCase.activateUser(userId);
            case INACTIVE -> changeUserStatusUseCase.deactivateUser(userId);
            case SUSPENDED -> changeUserStatusUseCase.suspendUser(userId);
        };
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userApplicationService.deleteUser(UserId.of(id));
        return ResponseEntity.noContent().build();
    }
}
