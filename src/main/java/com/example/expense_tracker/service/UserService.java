package com.example.expense_tracker.service;

import com.example.expense_tracker.dto.request.UpdateUserRequest;
import com.example.expense_tracker.dto.request.UpdateUserStatusRequest;
import com.example.expense_tracker.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> getAll();
    UserResponse getById(Long id);
    UserResponse update(Long id, UpdateUserRequest request);
    UserResponse updateStatus(Long id, UpdateUserStatusRequest request);
    UserResponse getCurrentUser(String email);
}
