package com.example.expense_tracker.service;

import com.example.expense_tracker.dto.request.CreateExpenseRequest;
import com.example.expense_tracker.dto.request.UpdateExpenseRequest;
import com.example.expense_tracker.dto.response.ExpenseResponse;
import com.example.expense_tracker.enums.ExpenseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseService {
    ExpenseResponse create(CreateExpenseRequest request, String email);
    List<ExpenseResponse> getMyExpenses(String email);
    ExpenseResponse getById(Long id, String email);
    ExpenseResponse update(Long id, UpdateExpenseRequest request, String email);
    void delete(Long id, String email);
    Page<ExpenseResponse> getAllFiltered(
            ExpenseStatus status,
            Long categoryId,
            Long departmentId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );
    ExpenseResponse markAsPaid(Long id);
}
