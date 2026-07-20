package com.ejada.ecommerce.controller;

import com.ejada.ecommerce.dto.AuthDto;
import com.ejada.ecommerce.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ejada.ecommerce.dto.TokenRefreshRequest;
import com.ejada.ecommerce.dto.TokenRefreshResponse;
import com.ejada.ecommerce.security.SessionTokenService;
import com.ejada.ecommerce.security.JwtService;
import com.ejada.ecommerce.entity.SessionToken;
import com.ejada.ecommerce.security.CustomUserDetails;
import com.ejada.ecommerce.exception.InvalidSessionException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public class AuthController {

    private final AuthService authService;
    private final SessionTokenService SessionTokenService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, SessionTokenService SessionTokenService, JwtService jwtService) {
        this.authService = authService;
        this.SessionTokenService = SessionTokenService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<AuthDto.AuthResponse> register(@Valid @RequestBody AuthDto.RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/register-admin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Register a new admin (Super Admin only)")
    public ResponseEntity<AuthDto.AuthResponse> registerAdmin(@Valid @RequestBody AuthDto.RegisterRequest request) {
        return ResponseEntity.ok(authService.registerAdmin(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Login and get tokens")
    public ResponseEntity<AuthDto.AuthResponse> login(@Valid @RequestBody AuthDto.LoginRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token using session token")
    public ResponseEntity<TokenRefreshResponse> SessionToken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestSessionToken = request.getSessionToken();

        return SessionTokenService.findByToken(requestSessionToken)
                .map(SessionTokenService::verifyExpiration)
                .map(SessionToken::getUser)
                .map(user -> {
                    String token = jwtService.generateToken(new CustomUserDetails(user));
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestSessionToken));
                })
                .orElseThrow(() -> new InvalidSessionException("Refresh token is not in database!"));
    }
}
