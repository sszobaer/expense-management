package com.example.expense_tracker.service.impl;

import com.example.expense_tracker.dto.request.CreateExpenseRequest;
import com.example.expense_tracker.dto.request.UpdateExpenseRequest;
import com.example.expense_tracker.dto.response.ExpenseResponse;
import com.example.expense_tracker.entity.Expense;
import com.example.expense_tracker.entity.ExpenseCategory;
import com.example.expense_tracker.entity.User;
import com.example.expense_tracker.enums.ExpenseStatus;
import com.example.expense_tracker.exception.BadRequestException;
import com.example.expense_tracker.exception.ResourceNotFoundException;
import com.example.expense_tracker.exception.UnauthorizedOperationException;
import com.example.expense_tracker.mapper.ExpenseMapper;
import com.example.expense_tracker.repository.ExpenseCategoryRepository;
import com.example.expense_tracker.repository.ExpenseRepository;
import com.example.expense_tracker.repository.UserRepository;
import com.example.expense_tracker.service.ExpenseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final ExpenseCategoryRepository categoryRepository;
    private final ExpenseMapper expenseMapper;

    public ExpenseServiceImpl(
            ExpenseRepository expenseRepository,
            UserRepository userRepository,
            ExpenseCategoryRepository categoryRepository,
            ExpenseMapper expenseMapper
    ) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.expenseMapper = expenseMapper;
    }

    @Override
    @Transactional
    public ExpenseResponse create(CreateExpenseRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ExpenseCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        Expense expense = new Expense();
        expense.setAmount(request.getAmount());
        expense.setDescription(request.getDescription());
        expense.setExpenseDate(request.getExpenseDate());
        expense.setReceiptUrl(request.getReceiptUrl());
        expense.setStatus(ExpenseStatus.PENDING);
        expense.setUser(user);
        expense.setCategory(category);

        return expenseMapper.toResponse(expenseRepository.save(expense));
    }

    @Override
    public List<ExpenseResponse> getMyExpenses(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return expenseRepository.findByUserId(user.getId())
                .stream()
                .map(expenseMapper::toResponse)
                .toList();
    }

    @Override
    public ExpenseResponse getById(Long id, String email) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));

        // Allow the owner or admin/manager/accountant roles
        User requester = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String roleName = requester.getRole() != null ? requester.getRole().getName() : "";
        boolean isPrivileged = roleName.equals("ADMIN") || roleName.equals("MANAGER") || roleName.equals("ACCOUNTANT");

        if (!isPrivileged && !expense.getUser().getId().equals(requester.getId())) {
            throw new UnauthorizedOperationException("You do not have permission to view this expense");
        }

        return expenseMapper.toResponse(expense);
    }

    @Override
    @Transactional
    public ExpenseResponse update(Long id, UpdateExpenseRequest request, String email) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));

        User requester = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!expense.getUser().getId().equals(requester.getId())) {
            throw new UnauthorizedOperationException("You can only update your own expenses");
        }

        if (expense.getStatus() != ExpenseStatus.PENDING) {
            throw new BadRequestException("Only PENDING expenses can be edited");
        }

        if (request.getAmount() != null) expense.setAmount(request.getAmount());
        if (request.getDescription() != null) expense.setDescription(request.getDescription());
        if (request.getExpenseDate() != null) expense.setExpenseDate(request.getExpenseDate());
        if (request.getReceiptUrl() != null) expense.setReceiptUrl(request.getReceiptUrl());

        if (request.getCategoryId() != null) {
            ExpenseCategory category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
            expense.setCategory(category);
        }

        return expenseMapper.toResponse(expenseRepository.save(expense));
    }

    @Override
    @Transactional
    public void delete(Long id, String email) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));

        User requester = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!expense.getUser().getId().equals(requester.getId())) {
            throw new UnauthorizedOperationException("You can only delete your own expenses");
        }

        if (expense.getStatus() != ExpenseStatus.PENDING) {
            throw new BadRequestException("Only PENDING expenses can be deleted");
        }

        expenseRepository.delete(expense);
    }

    @Override
    public Page<ExpenseResponse> getAllFiltered(
            ExpenseStatus status,
            Long categoryId,
            Long departmentId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {
        return expenseRepository.findAllFiltered(status, categoryId, departmentId, startDate, endDate, pageable)
                .map(expenseMapper::toResponse);
    }

    @Override
    @Transactional
    public ExpenseResponse markAsPaid(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));

        if (expense.getStatus() != ExpenseStatus.APPROVED) {
            throw new BadRequestException("Only APPROVED expenses can be marked as PAID");
        }

        expense.setStatus(ExpenseStatus.PAID);
        return expenseMapper.toResponse(expenseRepository.save(expense));
    }
}
