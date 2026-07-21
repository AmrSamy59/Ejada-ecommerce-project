package com.ejada.ecommerce.controller;

import com.ejada.ecommerce.dto.auth.AuthResponse;
import com.ejada.ecommerce.dto.auth.LoginRequest;
import com.ejada.ecommerce.dto.auth.RegisterRequest;
import com.ejada.ecommerce.dto.auth.TokenRefreshRequest;
import com.ejada.ecommerce.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ejada.ecommerce.security.RefreshTokenService;
import com.ejada.ecommerce.security.JwtService;
import com.ejada.ecommerce.entity.RefreshToken;
import com.ejada.ecommerce.security.CustomUserDetails;
import com.ejada.ecommerce.exception.InvalidSessionException;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/register-admin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Register a new admin (Super Admin only)")
    public ResponseEntity<AuthResponse> registerAdmin(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.registerAdmin(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Login and get tokens")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token using refresh token")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtService.generateToken(new CustomUserDetails(user));
                    return ResponseEntity.ok(new AuthResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new InvalidSessionException("Refresh token is not in database!"));
    }
}
