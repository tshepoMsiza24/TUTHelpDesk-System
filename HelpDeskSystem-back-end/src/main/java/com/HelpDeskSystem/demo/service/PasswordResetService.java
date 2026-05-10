package com.HelpDeskSystem.demo.service;

import com.HelpDeskSystem.demo.dto.request.ChangePasswordRequest;
import com.HelpDeskSystem.demo.dto.request.ForgotPasswordRequest;
import com.HelpDeskSystem.demo.dto.request.ResetPasswordRequest;
import com.HelpDeskSystem.demo.model.User;
import com.HelpDeskSystem.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PasswordResetService {

    // token -> { userId, expiresAt }
    private final Map<String, TokenEntry> tokenStore = new ConcurrentHashMap<>();

    // Token valid for 15 minutes
    private static final long TOKEN_TTL_MS = 15 * 60 * 1000L;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ── FORGOT PASSWORD ──────────────────────────────────────────────────────
    /**
     * Verifies username + email match, generates a short-lived reset token.
     * Returns the token directly (in a real system this would be emailed).
     */
    public String generateResetToken(ForgotPasswordRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("No account found with that username."));

        if (!user.getEmail().equalsIgnoreCase(request.getEmail())) {
            throw new RuntimeException("Username and email do not match.");
        }

        // Invalidate any existing token for this user
        tokenStore.entrySet().removeIf(e -> e.getValue().userId.equals(user.getId()));

        String token = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        tokenStore.put(token, new TokenEntry(user.getId(), Instant.now().plusMillis(TOKEN_TTL_MS)));

        return token;
    }

    // ── RESET PASSWORD ───────────────────────────────────────────────────────
    public void resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match.");
        }

        TokenEntry entry = tokenStore.get(request.getResetToken().toUpperCase());
        if (entry == null) {
            throw new RuntimeException("Invalid reset token.");
        }
        if (Instant.now().isAfter(entry.expiresAt)) {
            tokenStore.remove(request.getResetToken().toUpperCase());
            throw new RuntimeException("Reset token has expired. Please request a new one.");
        }

        User user = userRepository.findById(entry.userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Consume the token
        tokenStore.remove(request.getResetToken().toUpperCase());
    }

    // ── CHANGE PASSWORD (authenticated) ─────────────────────────────────────
    public void changePassword(String username, ChangePasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("New passwords do not match.");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found."));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect.");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new RuntimeException("New password must be different from your current password.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    // ── INNER CLASS ──────────────────────────────────────────────────────────
    private static class TokenEntry {
        final Long userId;
        final Instant expiresAt;

        TokenEntry(Long userId, Instant expiresAt) {
            this.userId = userId;
            this.expiresAt = expiresAt;
        }
    }
}
