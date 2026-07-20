package com.ejada.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenRefreshResponse {
    private String accessToken;
    private String SessionToken;
    private String tokenType;

    public TokenRefreshResponse(String accessToken, String SessionToken) {
        this.accessToken = accessToken;
        this.SessionToken = SessionToken;
        this.tokenType = "Bearer";
    }
}
