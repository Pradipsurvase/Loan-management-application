package com.example.educationloan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Returned to the client after successful login, register, or token refresh.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthDTO {
    private String accessToken;
    private String refreshToken;
    private String username;
    private String tokenType = "Bearer";


    public AuthDTO(String accessToken, String refreshToken, String username) {
        this.accessToken  = accessToken;
        this.refreshToken = refreshToken;
        this.username     = username;
    }
}