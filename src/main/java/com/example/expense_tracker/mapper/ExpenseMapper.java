package com.example.expense_tracker.mapper;

import com.example.expense_tracker.dto.response.ExpenseApprovalResponse;
import com.example.expense_tracker.dto.response.ExpenseResponse;
import com.example.expense_tracker.entity.Expense;
import com.example.expense_tracker.entity.ExpenseApproval;
import org.springframework.stereotype.Component;

@Component
public class ExpenseMapper {

    public ExpenseResponse toResponse(Expense expense) {
        ExpenseResponse response = new ExpenseResponse();
        response.setId(expense.getId());
        response.setAmount(expense.getAmount());
        response.setDescription(expense.getDescription());
        response.setExpenseDate(expense.getExpenseDate());
        response.setReceiptUrl(expense.getReceiptUrl());
        response.setStatus(expense.getStatus() != null ? expense.getStatus().name() : null);
        response.setCreatedAt(expense.getCreatedAt());
        response.setUpdatedAt(expense.getUpdatedAt());

        if (expense.getUser() != null) {
            response.setUserId(expense.getUser().getId());
            response.setUserName(expense.getUser().getName());
        }
        if (expense.getCategory() != null) {
            response.setCategoryId(expense.getCategory().getId());
            response.setCategoryName(expense.getCategory().getName());
        }
        return response;
    }

    public ExpenseApprovalResponse toApprovalResponse(ExpenseApproval approval) {
        ExpenseApprovalResponse response = new ExpenseApprovalResponse();
        response.setId(approval.getId());
        response.setExpenseId(approval.getExpense() != null ? approval.getExpense().getId() : null);
        response.setAction(approval.getAction() != null ? approval.getAction().name() : null);
        response.setComment(approval.getComment());
        response.setApprovedAt(approval.getApprovedAt());

        if (approval.getApprover() != null) {
            response.setApproverId(approval.getApprover().getId());
            response.setApproverName(approval.getApprover().getName());
        }
        return response;
    }
}
