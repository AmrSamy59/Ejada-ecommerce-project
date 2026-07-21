package com.ejada.ecommerce.service;

import com.ejada.ecommerce.dto.auth.AuthResponse;
import com.ejada.ecommerce.dto.auth.LoginRequest;
import com.ejada.ecommerce.repository.UserRepository;
import com.ejada.ecommerce.security.CustomUserDetails;
import com.ejada.ecommerce.security.JwtService;
import com.ejada.ecommerce.security.RefreshTokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;



    public AuthResponse authenticate(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        var jwtToken = jwtService.generateToken(new CustomUserDetails(user));
        
        // Delete old refresh tokens if desired, or just create a new one
        refreshTokenService.deleteByUserId(user.getId());
        var refreshToken = refreshTokenService.createRefreshToken(user.getId());
        
        return new AuthResponse(jwtToken, refreshToken.getToken());
    }
}
