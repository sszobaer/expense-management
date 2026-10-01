package com.example.expense_tracker.service;

import com.example.expense_tracker.dto.request.CreateBudgetRequest;
import com.example.expense_tracker.dto.request.UpdateBudgetRequest;
import com.example.expense_tracker.dto.response.BudgetResponse;
import com.example.expense_tracker.dto.response.BudgetSummaryResponse;

import java.util.List;

public interface BudgetService {
    BudgetResponse create(CreateBudgetRequest request);
    List<BudgetResponse> getAll();
    BudgetResponse getById(Long id);
    BudgetResponse update(Long id, UpdateBudgetRequest request);
    void delete(Long id);
    List<BudgetResponse> getByDepartment(Long departmentId);
    BudgetSummaryResponse getSummary(Long departmentId, int month, int year);
}
