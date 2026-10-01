package com.example.expense_tracker.service.impl;

import com.example.expense_tracker.dto.request.ApprovalRequest;
import com.example.expense_tracker.dto.response.ExpenseApprovalResponse;
import com.example.expense_tracker.dto.response.ExpenseResponse;
import com.example.expense_tracker.entity.Expense;
import com.example.expense_tracker.entity.ExpenseApproval;
import com.example.expense_tracker.entity.User;
import com.example.expense_tracker.enums.ApprovalStatus;
import com.example.expense_tracker.enums.ExpenseStatus;
import com.example.expense_tracker.exception.BadRequestException;
import com.example.expense_tracker.exception.ResourceNotFoundException;
import com.example.expense_tracker.exception.UnauthorizedOperationException;
import com.example.expense_tracker.mapper.ExpenseMapper;
import com.example.expense_tracker.repository.ExpenseApprovalRepository;
import com.example.expense_tracker.repository.ExpenseRepository;
import com.example.expense_tracker.repository.UserRepository;
import com.example.expense_tracker.service.ExpenseApprovalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExpenseApprovalServiceImpl implements ExpenseApprovalService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseApprovalRepository approvalRepository;
    private final UserRepository userRepository;
    private final ExpenseMapper expenseMapper;

    public ExpenseApprovalServiceImpl(
            ExpenseRepository expenseRepository,
            ExpenseApprovalRepository approvalRepository,
            UserRepository userRepository,
            ExpenseMapper expenseMapper
    ) {
        this.expenseRepository = expenseRepository;
        this.approvalRepository = approvalRepository;
        this.userRepository = userRepository;
        this.expenseMapper = expenseMapper;
    }

    @Override
    @Transactional
    public ExpenseResponse approve(Long expenseId, ApprovalRequest request, String approverEmail) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + expenseId));

        if (expense.getStatus() != ExpenseStatus.PENDING) {
            throw new BadRequestException("Only PENDING expenses can be approved");
        }

        User approver = userRepository.findByEmail(approverEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        expense.setStatus(ExpenseStatus.APPROVED);
        expenseRepository.save(expense);

        ExpenseApproval approval = new ExpenseApproval();
        approval.setExpense(expense);
        approval.setApprover(approver);
        approval.setAction(ApprovalStatus.APPROVED);
        approval.setComment(request != null ? request.getComment() : null);
        approval.setApprovedAt(LocalDateTime.now());
        approvalRepository.save(approval);

        return expenseMapper.toResponse(expense);
    }

    @Override
    @Transactional
    public ExpenseResponse reject(Long expenseId, ApprovalRequest request, String approverEmail) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + expenseId));

        if (expense.getStatus() != ExpenseStatus.PENDING) {
            throw new BadRequestException("Only PENDING expenses can be rejected");
        }

        User approver = userRepository.findByEmail(approverEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        expense.setStatus(ExpenseStatus.REJECTED);
        expenseRepository.save(expense);

        ExpenseApproval approval = new ExpenseApproval();
        approval.setExpense(expense);
        approval.setApprover(approver);
        approval.setAction(ApprovalStatus.REJECTED);
        approval.setComment(request != null ? request.getComment() : null);
        approval.setApprovedAt(LocalDateTime.now());
        approvalRepository.save(approval);

        return expenseMapper.toResponse(expense);
    }

    @Override
    public List<ExpenseApprovalResponse> getApprovalHistory(Long expenseId, String requesterEmail) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + expenseId));

        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String roleName = requester.getRole() != null ? requester.getRole().getName() : "";
        boolean isEmployee = roleName.equals("EMPLOYEE");

        // Employees can only view approval history for their own expenses
        if (isEmployee && !expense.getUser().getId().equals(requester.getId())) {
            throw new UnauthorizedOperationException("You can only view approval history for your own expenses");
        }

        return approvalRepository.findByExpenseId(expenseId)
                .stream()
                .map(expenseMapper::toApprovalResponse)
                .toList();
    }
}
