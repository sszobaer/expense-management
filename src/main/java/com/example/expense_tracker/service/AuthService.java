package com.example.expense_tracker.service;

import com.example.expense_tracker.dto.auth.request.LoginRequest;
import com.example.expense_tracker.dto.auth.request.RegisterRequest;
import com.example.expense_tracker.dto.auth.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}