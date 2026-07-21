package com.ejada.ecommerce.dto.auth;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String refreshToken;
    private String type = "Bearer";

    public AuthResponse(String token, String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
    }
}
