package com.example.expense_tracker.service;

import com.example.expense_tracker.dto.request.CreateExpenseCategoryRequest;
import com.example.expense_tracker.dto.request.UpdateExpenseCategoryRequest;
import com.example.expense_tracker.dto.response.ExpenseCategoryResponse;

import java.util.List;

public interface ExpenseCategoryService {
    ExpenseCategoryResponse create(CreateExpenseCategoryRequest request);
    List<ExpenseCategoryResponse> getAll();
    ExpenseCategoryResponse getById(Long id);
    ExpenseCategoryResponse update(Long id, UpdateExpenseCategoryRequest request);
    void delete(Long id);
}
