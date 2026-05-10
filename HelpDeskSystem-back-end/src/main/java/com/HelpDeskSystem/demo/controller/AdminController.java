package com.HelpDeskSystem.demo.controller;

import com.HelpDeskSystem.demo.dto.request.RegisterRequest;
import com.HelpDeskSystem.demo.dto.request.UpdateUserRequest;
import com.HelpDeskSystem.demo.dto.response.AuthResponse;
import com.HelpDeskSystem.demo.dto.response.UserResponse;
import com.HelpDeskSystem.demo.model.User;
import com.HelpDeskSystem.demo.repository.UserRepository;
import com.HelpDeskSystem.demo.service.AuthService;
import com.HelpDeskSystem.demo.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Admin", description = "Admin management endpoints")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/users")
    @Operation(summary = "Get all users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userRepository.findAll().stream()
                .map(u -> UserResponse.builder()
                        .id(u.getId()).username(u.getUsername()).email(u.getEmail())
                        .fullName(u.getFullName()).role(u.getRole().name()).createdAt(u.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/users/students")
    @Operation(summary = "Get all students")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllStudents() {
        List<UserResponse> students = userRepository.findByRole(User.Role.STUDENT).stream()
                .map(u -> UserResponse.builder()
                        .id(u.getId()).username(u.getUsername()).email(u.getEmail())
                        .fullName(u.getFullName()).role(u.getRole().name()).createdAt(u.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(students));
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new admin user")
    public ResponseEntity<ApiResponse<AuthResponse>> registerAdmin(@Valid @RequestBody RegisterRequest request) {
        request.setRole("ADMIN");
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("Admin registered successfully", response));
    }

    @PutMapping("/users/{id}")
    @Operation(summary = "Update a student's details")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Prevent updating another admin
        if (user.getRole() == User.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Cannot modify admin accounts"));
        }

        // Check username uniqueness if changed
        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            if (!request.getUsername().equals(user.getUsername())
                    && userRepository.existsByUsername(request.getUsername())) {
                throw new RuntimeException("Username '" + request.getUsername() + "' is already taken");
            }
            user.setUsername(request.getUsername());
        }

        // Check email uniqueness if changed
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (!request.getEmail().equals(user.getEmail())
                    && userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email '" + request.getEmail() + "' is already in use");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User saved = userRepository.save(user);

        UserResponse response = UserResponse.builder()
                .id(saved.getId())
                .username(saved.getUsername())
                .email(saved.getEmail())
                .fullName(saved.getFullName())
                .role(saved.getRole().name())
                .createdAt(saved.getCreatedAt())
                .build();

        return ResponseEntity.ok(ApiResponse.success("User updated successfully", response));
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Delete a student account")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (user.getRole() == User.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Cannot delete admin accounts"));
        }

        userRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }
}
