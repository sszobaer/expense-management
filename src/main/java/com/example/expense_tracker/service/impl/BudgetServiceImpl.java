package com.example.expense_tracker.service.impl;

import com.example.expense_tracker.dto.request.CreateBudgetRequest;
import com.example.expense_tracker.dto.request.UpdateBudgetRequest;
import com.example.expense_tracker.dto.response.BudgetResponse;
import com.example.expense_tracker.dto.response.BudgetSummaryResponse;
import com.example.expense_tracker.entity.Budget;
import com.example.expense_tracker.entity.Department;
import com.example.expense_tracker.enums.ExpenseStatus;
import com.example.expense_tracker.exception.ResourceNotFoundException;
import com.example.expense_tracker.mapper.BudgetMapper;
import com.example.expense_tracker.repository.BudgetRepository;
import com.example.expense_tracker.repository.DepartmentRepository;
import com.example.expense_tracker.repository.ExpenseRepository;
import com.example.expense_tracker.service.BudgetService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final DepartmentRepository departmentRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetMapper budgetMapper;

    public BudgetServiceImpl(
            BudgetRepository budgetRepository,
            DepartmentRepository departmentRepository,
            ExpenseRepository expenseRepository,
            BudgetMapper budgetMapper
    ) {
        this.budgetRepository = budgetRepository;
        this.departmentRepository = departmentRepository;
        this.expenseRepository = expenseRepository;
        this.budgetMapper = budgetMapper;
    }

    @Override
    public BudgetResponse create(CreateBudgetRequest request) {
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + request.getDepartmentId()));

        Budget budget = new Budget();
        budget.setDepartment(department);
        budget.setAmount(request.getAmount());
        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());

        return budgetMapper.toResponse(budgetRepository.save(budget));
    }

    @Override
    public List<BudgetResponse> getAll() {
        return budgetRepository.findAll()
                .stream()
                .map(budgetMapper::toResponse)
                .toList();
    }

    @Override
    public BudgetResponse getById(Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + id));
        return budgetMapper.toResponse(budget);
    }

    @Override
    public BudgetResponse update(Long id, UpdateBudgetRequest request) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + id));

        if (request.getAmount() != null) budget.setAmount(request.getAmount());
        if (request.getMonth() != null) budget.setMonth(request.getMonth());
        if (request.getYear() != null) budget.setYear(request.getYear());

        return budgetMapper.toResponse(budgetRepository.save(budget));
    }

    @Override
    public void delete(Long id) {
        if (!budgetRepository.existsById(id)) {
            throw new ResourceNotFoundException("Budget not found with id: " + id);
        }
        budgetRepository.deleteById(id);
    }

    @Override
    public List<BudgetResponse> getByDepartment(Long departmentId) {
        if (!departmentRepository.existsById(departmentId)) {
            throw new ResourceNotFoundException("Department not found with id: " + departmentId);
        }
        return budgetRepository.findByDepartmentId(departmentId)
                .stream()
                .map(budgetMapper::toResponse)
                .toList();
    }

    /**
     * Budget summary for a given department, month, year.
     *
     * approvedExpenses: sum of expenses with status APPROVED or PAID
     *   (i.e., all expenses that have been approved regardless of payment)
     * paidExpenses: sum of expenses with status PAID only
     * remainingBudget: budget amount - approvedExpenses
     */
    @Override
    public BudgetSummaryResponse getSummary(Long departmentId, int month, int year) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + departmentId));

        Budget budget = budgetRepository.findByDepartmentIdAndMonthAndYear(departmentId, month, year)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No budget found for department " + departmentId + " in " + month + "/" + year));

        BigDecimal approvedExpenses = expenseRepository.sumByDepartmentAndStatusInAndMonthAndYear(
                departmentId,
                List.of(ExpenseStatus.APPROVED, ExpenseStatus.PAID),
                month,
                year
        );

        BigDecimal paidExpenses = expenseRepository.sumByDepartmentAndStatusInAndMonthAndYear(
                departmentId,
                List.of(ExpenseStatus.PAID),
                month,
                year
        );

        BigDecimal remainingBudget = budget.getAmount().subtract(approvedExpenses);

        BudgetSummaryResponse response = new BudgetSummaryResponse();
        response.setDepartment(department.getName());
        response.setMonth(month);
        response.setYear(year);
        response.setBudget(budget.getAmount());
        response.setApprovedExpenses(approvedExpenses);
        response.setPaidExpenses(paidExpenses);
        response.setRemainingBudget(remainingBudget);

        return response;
    }
}
