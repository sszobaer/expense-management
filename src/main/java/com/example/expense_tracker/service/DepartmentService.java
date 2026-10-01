package com.example.expense_tracker.service;

import com.example.expense_tracker.dto.request.CreateDepartmentRequest;
import com.example.expense_tracker.dto.request.UpdateDepartmentRequest;
import com.example.expense_tracker.dto.response.DepartmentResponse;

import java.util.List;

public interface DepartmentService {
    DepartmentResponse create(CreateDepartmentRequest request);
    List<DepartmentResponse> getAll();
    DepartmentResponse getById(Long id);
    DepartmentResponse update(Long id, UpdateDepartmentRequest request);
    void delete(Long id);
}
