package com.example.expense_tracker.dto.request;

import jakarta.validation.constraints.NotBlank;

public class CreateExpenseCategoryRequest {

    @NotBlank(message = "Category name is required")
    private String name;

    private String description;

    public CreateExpenseCategoryRequest() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
