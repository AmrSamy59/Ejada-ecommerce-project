package com.ejada.ecommerce.service;

import com.ejada.ecommerce.dto.auth.AuthResponse;
import com.ejada.ecommerce.dto.auth.LoginRequest;
import com.ejada.ecommerce.dto.auth.RegisterRequest;
import com.ejada.ecommerce.exception.ResourceConflictException;
import com.ejada.ecommerce.security.RefreshTokenService;
import com.ejada.ecommerce.entity.Role;
import com.ejada.ecommerce.entity.User;
import com.ejada.ecommerce.repository.RoleRepository;
import com.ejada.ecommerce.repository.UserRepository;
import com.ejada.ecommerce.security.CustomUserDetails;
import com.ejada.ecommerce.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder, JwtService jwtService,
                       AuthenticationManager authenticationManager,
                       RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResourceConflictException("Error: Username is already taken!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceConflictException("Error: Email is already in use!");
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(Role.builder().name("USER").build()));

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .roles(Set.of(userRole))
                .build();

        userRepository.save(user);

        var jwtToken = jwtService.generateToken(new CustomUserDetails(user));
        var refreshToken = refreshTokenService.createRefreshToken(user.getId());
        return new AuthResponse(jwtToken, refreshToken.getToken());
    }

    public AuthResponse registerAdmin(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResourceConflictException("Error: Username is already taken!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceConflictException("Error: Email is already in use!");
        }

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ADMIN").build()));

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .roles(Set.of(adminRole))
                .build();

        userRepository.save(user);

        var jwtToken = jwtService.generateToken(new CustomUserDetails(user));
        var refreshToken = refreshTokenService.createRefreshToken(user.getId());
        return new AuthResponse(jwtToken, refreshToken.getToken());
    }

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
