package com.ejada.ecommerce.controller;

import com.ejada.ecommerce.dto.auth.AuthResponse;
import com.ejada.ecommerce.dto.auth.RegisterRequest;
import com.ejada.ecommerce.dto.user.UpdateUserRequest;
import com.ejada.ecommerce.dto.user.UserResponse;
import com.ejada.ecommerce.dto.common.PageResponse;
import com.ejada.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.ejada.ecommerce.security.CustomUserDetails;

import static com.ejada.ecommerce.entity.RoleName.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints for user registration and management")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get current authenticated user profile")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(userService.getUserById(userDetails.getId()));
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/register-admin")
    @PreAuthorize("hasRole('" + SUPER_ADMIN_STR + "')")
    @Operation(summary = "Register a new admin (Super Admin only)")
    public ResponseEntity<AuthResponse> registerAdmin(@Valid @RequestBody RegisterRequest request) {
        return new ResponseEntity<>(userService.registerAdmin(request), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('" + ADMIN_STR + "', '" + SUPER_ADMIN_STR + "')")
    @Operation(summary = "Get all users with optional filters (Admin only)")
    public ResponseEntity<PageResponse<UserResponse>> getAllUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.getAllUsers(name, email, isActive, page, size));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('" + ADMIN_STR + "', '" + SUPER_ADMIN_STR + "')")
    @Operation(summary = "Edit a user's details (Admin only)")
    public ResponseEntity<UserResponse> editUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.editUser(id, request));
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('" + ADMIN_STR + "', '" + SUPER_ADMIN_STR + "')")
    @Operation(summary = "Activate a user account (Admin only)")
    public ResponseEntity<UserResponse> activateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.updateUserStatus(id, true));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('" + ADMIN_STR + "', '" + SUPER_ADMIN_STR + "')")
    @Operation(summary = "Deactivate a user account (Admin only)")
    public ResponseEntity<UserResponse> deactivateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.updateUserStatus(id, false));
    }
}
