package com.HelpDeskSystem.demo.controller;

import com.HelpDeskSystem.demo.dto.request.RegisterRequest;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
}
