package com.example.expense_tracker.dto.auth.response;

public class AuthResponse {
    private String token;
    private String tokenType;
    private String email;
    private String role;

    public AuthResponse(
            String token,
            String tokenType,
            String email,
            String role
    ) {
        this.token = token;
        this.tokenType = tokenType;
        this.email = email;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}
