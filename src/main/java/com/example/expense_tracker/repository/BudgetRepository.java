package com.example.expense_tracker.repository;

import com.example.expense_tracker.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByDepartmentId(Long departmentId);

    Optional<Budget> findByDepartmentIdAndMonthAndYear(Long departmentId, int month, int year);
}
