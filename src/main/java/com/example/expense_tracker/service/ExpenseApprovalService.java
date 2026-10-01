package com.example.expense_tracker.service;

import com.example.expense_tracker.dto.request.ApprovalRequest;
import com.example.expense_tracker.dto.response.ExpenseApprovalResponse;
import com.example.expense_tracker.dto.response.ExpenseResponse;

import java.util.List;

public interface ExpenseApprovalService {
    ExpenseResponse approve(Long expenseId, ApprovalRequest request, String approverEmail);
    ExpenseResponse reject(Long expenseId, ApprovalRequest request, String approverEmail);
    List<ExpenseApprovalResponse> getApprovalHistory(Long expenseId, String requesterEmail);
}
