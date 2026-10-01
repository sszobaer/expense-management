package com.example.expense_tracker.dto.response;

import java.math.BigDecimal;

/**
 * Budget summary response.
 * approvedExpenses: total of APPROVED + PAID expenses for the department in the given month/year.
 * paidExpenses: total of PAID expenses for the department in the given month/year.
 * remainingBudget: budget amount - approvedExpenses (includes both APPROVED and PAID).
 */
public class BudgetSummaryResponse {

    private String department;
    private Integer month;
    private Integer year;
    private BigDecimal budget;
    private BigDecimal approvedExpenses;
    private BigDecimal paidExpenses;
    private BigDecimal remainingBudget;

    public BudgetSummaryResponse() {}

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public BigDecimal getBudget() { return budget; }
    public void setBudget(BigDecimal budget) { this.budget = budget; }

    public BigDecimal getApprovedExpenses() { return approvedExpenses; }
    public void setApprovedExpenses(BigDecimal approvedExpenses) { this.approvedExpenses = approvedExpenses; }

    public BigDecimal getPaidExpenses() { return paidExpenses; }
    public void setPaidExpenses(BigDecimal paidExpenses) { this.paidExpenses = paidExpenses; }

    public BigDecimal getRemainingBudget() { return remainingBudget; }
    public void setRemainingBudget(BigDecimal remainingBudget) { this.remainingBudget = remainingBudget; }
}
