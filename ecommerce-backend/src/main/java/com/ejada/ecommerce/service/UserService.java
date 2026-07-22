package com.ejada.ecommerce.service;

import com.ejada.ecommerce.dto.auth.AuthResponse;
import com.ejada.ecommerce.dto.auth.RegisterRequest;
import com.ejada.ecommerce.dto.common.PageResponse;
import com.ejada.ecommerce.dto.user.UpdateUserRequest;
import com.ejada.ecommerce.dto.user.UserResponse;
import com.ejada.ecommerce.entity.Role;
import com.ejada.ecommerce.entity.User;
import com.ejada.ecommerce.exception.ResourceConflictException;
import com.ejada.ecommerce.exception.ResourceNotFoundException;
import com.ejada.ecommerce.exception.ErrorCode;
import com.ejada.ecommerce.mapper.UserMapper;
import com.ejada.ecommerce.repository.RoleRepository;
import com.ejada.ecommerce.repository.UserRepository;
import com.ejada.ecommerce.security.CustomUserDetails;
import com.ejada.ecommerce.security.JwtService;
import com.ejada.ecommerce.security.RefreshTokenService;
import com.ejada.ecommerce.specification.UserSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserMapper userMapper;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResourceConflictException("Error: Username is already taken!", ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceConflictException("Error: Email is already in use!", ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(Role.builder().name("USER").build()));

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of(userRole));
        user.setActive(true);

        userRepository.save(user);

        var jwtToken = jwtService.generateToken(new CustomUserDetails(user));
        var refreshToken = refreshTokenService.createRefreshToken(user.getId());
        return new AuthResponse(jwtToken, refreshToken.getToken());
    }

    public AuthResponse registerAdmin(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResourceConflictException("Error: Username is already taken!", ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceConflictException("Error: Email is already in use!", ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ADMIN").build()));

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of(adminRole));
        user.setActive(true);

        userRepository.save(user);

        var jwtToken = jwtService.generateToken(new CustomUserDetails(user));
        var refreshToken = refreshTokenService.createRefreshToken(user.getId());
        return new AuthResponse(jwtToken, refreshToken.getToken());
    }

    public UserResponse editUser(Long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId, ErrorCode.USER_NOT_FOUND));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        
        // If email changed, check if it's already taken
        if (!user.getEmail().equals(request.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new ResourceConflictException("Error: Email is already in use!", ErrorCode.EMAIL_ALREADY_EXISTS);
            }
            user.setEmail(request.getEmail());
        }

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    public PageResponse<UserResponse> getAllUsers(String name, String email, Boolean isActive, int pageNo, int pageSize) {
        Specification<User> spec = UserSpecification.filterUsers(name, email, isActive);
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<User> users = userRepository.findAll(spec, pageable);
        
        List<UserResponse> content = users.getContent().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
                
        return new PageResponse<>(content, users.getNumber(), users.getSize(), users.getTotalElements(), users.getTotalPages(), users.isLast());
    }

    public UserResponse updateUserStatus(Long userId, boolean isActive) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId, ErrorCode.USER_NOT_FOUND));

        user.setActive(isActive);
        
        // If user is deactivated, delete refresh tokens
        if (!isActive) {
            refreshTokenService.deleteByUserId(user.getId());
        }

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
}
