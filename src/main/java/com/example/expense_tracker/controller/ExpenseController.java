package com.example.expense_tracker.controller;

import com.example.expense_tracker.dto.request.ApprovalRequest;
import com.example.expense_tracker.dto.request.CreateExpenseRequest;
import com.example.expense_tracker.dto.request.UpdateExpenseRequest;
import com.example.expense_tracker.dto.response.ExpenseApprovalResponse;
import com.example.expense_tracker.dto.response.ExpenseResponse;
import com.example.expense_tracker.enums.ExpenseStatus;
import com.example.expense_tracker.service.ExpenseApprovalService;
import com.example.expense_tracker.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final ExpenseApprovalService approvalService;

    public ExpenseController(
            ExpenseService expenseService,
            ExpenseApprovalService approvalService
    ) {
        this.expenseService = expenseService;
        this.approvalService = approvalService;
    }

    // ── Employee endpoints ──────────────────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ExpenseResponse> create(
            @Valid @RequestBody CreateExpenseRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(expenseService.create(request, userDetails.getUsername()));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<List<ExpenseResponse>> getMyExpenses(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(expenseService.getMyExpenses(userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponse> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(expenseService.getById(id, userDetails.getUsername()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ExpenseResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateExpenseRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(expenseService.update(id, request, userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        expenseService.delete(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    // ── Admin / Manager / Accountant endpoints ──────────────────────────────

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'ACCOUNTANT')")
    public ResponseEntity<Page<ExpenseResponse>> getAll(
            @RequestParam(required = false) ExpenseStatus status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                expenseService.getAllFiltered(status, categoryId, departmentId, startDate, endDate, pageable)
        );
    }

    // ── Approval endpoints ──────────────────────────────────────────────────

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ExpenseResponse> approve(
            @PathVariable Long id,
            @RequestBody(required = false) ApprovalRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(approvalService.approve(id, request, userDetails.getUsername()));
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ExpenseResponse> reject(
            @PathVariable Long id,
            @RequestBody(required = false) ApprovalRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(approvalService.reject(id, request, userDetails.getUsername()));
    }

    @GetMapping("/{id}/approvals")
    public ResponseEntity<List<ExpenseApprovalResponse>> getApprovals(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(approvalService.getApprovalHistory(id, userDetails.getUsername()));
    }

    // ── Payment endpoint ────────────────────────────────────────────────────

    @PatchMapping("/{id}/pay")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<ExpenseResponse> pay(@PathVariable Long id) {
        return ResponseEntity.ok(expenseService.markAsPaid(id));
    }
}
