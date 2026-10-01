package com.example.expense_tracker.mapper;

import com.example.expense_tracker.dto.response.DepartmentResponse;
import com.example.expense_tracker.entity.Department;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {

    public DepartmentResponse toResponse(Department department) {
        return new DepartmentResponse(
                department.getId(),
                department.getName()
        );
    }
}
