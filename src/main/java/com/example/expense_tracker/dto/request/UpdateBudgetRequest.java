package com.example.expense_tracker.dto.request;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class UpdateBudgetRequest {

    @Positive(message = "Amount must be greater than 0")
    private BigDecimal amount;

    private Integer month;
    private Integer year;

    public UpdateBudgetRequest() {}

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
}
