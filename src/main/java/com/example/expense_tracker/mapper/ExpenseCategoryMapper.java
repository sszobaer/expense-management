package com.example.expense_tracker.mapper;

import com.example.expense_tracker.dto.response.ExpenseCategoryResponse;
import com.example.expense_tracker.entity.ExpenseCategory;
import org.springframework.stereotype.Component;

@Component
public class ExpenseCategoryMapper {

    public ExpenseCategoryResponse toResponse(ExpenseCategory category) {
        return new ExpenseCategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }
}
