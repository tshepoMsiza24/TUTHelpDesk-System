package com.HelpDeskSystem.demo.controller;

import com.HelpDeskSystem.demo.dto.request.ChangePasswordRequest;
import com.HelpDeskSystem.demo.dto.request.ForgotPasswordRequest;
import com.HelpDeskSystem.demo.dto.request.LoginRequest;
import com.HelpDeskSystem.demo.dto.request.RegisterRequest;
import com.HelpDeskSystem.demo.dto.request.ResetPasswordRequest;
import com.HelpDeskSystem.demo.dto.response.AuthResponse;
import com.HelpDeskSystem.demo.dto.response.UserResponse;
import com.HelpDeskSystem.demo.service.AuthService;
import com.HelpDeskSystem.demo.service.PasswordResetService;
import com.HelpDeskSystem.demo.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user (Student)")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        // Force role to STUDENT for public registration
        request.setRole("STUDENT");
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("Registration successful", response));
    }

    @PostMapping("/login")
    @Operation(summary = "Login with username and password")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(Authentication authentication) {
        UserResponse user = authService.getCurrentUser(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    // ── FORGOT PASSWORD ──────────────────────────────────────────────────────
    @PostMapping("/forgot-password")
    @Operation(summary = "Verify username + email and get a reset token")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        String token = passwordResetService.generateResetToken(request);
        return ResponseEntity.ok(ApiResponse.success(
                "Reset token generated. Use it within 15 minutes.", token));
    }

    // ── RESET PASSWORD ───────────────────────────────────────────────────────
    @PostMapping("/reset-password")
    @Operation(summary = "Reset password using a valid reset token")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully.", null));
    }

    // ── CHANGE PASSWORD (authenticated) ─────────────────────────────────────
    @PostMapping("/change-password")
    @Operation(summary = "Change password for the currently logged-in user")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {
        passwordResetService.changePassword(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully.", null));
    }
}
