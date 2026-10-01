package com.example.expense_tracker.mapper;

import com.example.expense_tracker.dto.response.UserResponse;
import com.example.expense_tracker.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole() != null ? user.getRole().getName() : null);
        response.setDepartment(user.getDepartment() != null ? user.getDepartment().getName() : null);
        response.setActive(user.getActive());
        return response;
    }
}
