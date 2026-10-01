package com.example.expense_tracker.repository;

import com.example.expense_tracker.entity.ExpenseApproval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseApprovalRepository extends JpaRepository<ExpenseApproval, Long> {
    List<ExpenseApproval> findByExpenseId(Long expenseId);
}
