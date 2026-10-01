package com.example.expense_tracker.mapper;

import com.example.expense_tracker.dto.response.BudgetResponse;
import com.example.expense_tracker.entity.Budget;
import org.springframework.stereotype.Component;

@Component
public class BudgetMapper {

    public BudgetResponse toResponse(Budget budget) {
        BudgetResponse response = new BudgetResponse();
        response.setId(budget.getId());
        response.setAmount(budget.getAmount());
        response.setMonth(budget.getMonth());
        response.setYear(budget.getYear());

        if (budget.getDepartment() != null) {
            response.setDepartmentId(budget.getDepartment().getId());
            response.setDepartmentName(budget.getDepartment().getName());
        }
        return response;
    }
}
