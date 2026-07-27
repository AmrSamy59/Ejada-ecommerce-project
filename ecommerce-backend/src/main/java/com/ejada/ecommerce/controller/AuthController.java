package com.ejada.ecommerce.controller;

import com.ejada.ecommerce.dto.auth.AuthResponse;
import com.ejada.ecommerce.dto.auth.LoginRequest;
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
import com.ejada.ecommerce.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;



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
                .orElseThrow(() -> new InvalidSessionException("Refresh token is not in database!", ErrorCode.TOKEN_INVALID));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user and invalidate refresh tokens")
    public ResponseEntity<?> logout(@org.springframework.security.core.annotation.AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            refreshTokenService.deleteByUserId(userDetails.getId());
        }
        return ResponseEntity.ok(java.util.Map.of("message", "Log out successful!"));
    }
}
