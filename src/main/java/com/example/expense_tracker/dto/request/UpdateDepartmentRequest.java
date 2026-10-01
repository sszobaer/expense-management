package com.example.expense_tracker.dto.request;

import jakarta.validation.constraints.NotBlank;

public class UpdateDepartmentRequest {

    @NotBlank(message = "Department name is required")
    private String name;

    public UpdateDepartmentRequest() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
