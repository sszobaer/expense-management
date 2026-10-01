package com.example.expense_tracker.repository;

import com.example.expense_tracker.entity.Expense;
import com.example.expense_tracker.enums.ExpenseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUserId(Long userId);

    Page<Expense> findByStatus(ExpenseStatus status, Pageable pageable);

    Page<Expense> findAll(Pageable pageable);

    /**
     * Filtered query for admin/manager/accountant views.
     * All parameters are optional — pass null to ignore a filter.
     */
    @Query("""
            SELECT e FROM Expense e
            WHERE (:status IS NULL OR e.status = :status)
              AND (:categoryId IS NULL OR e.category.id = :categoryId)
              AND (:departmentId IS NULL OR e.user.department.id = :departmentId)
              AND (:startDate IS NULL OR e.expenseDate >= :startDate)
              AND (:endDate IS NULL OR e.expenseDate <= :endDate)
            """)
    Page<Expense> findAllFiltered(
            @Param("status") ExpenseStatus status,
            @Param("categoryId") Long categoryId,
            @Param("departmentId") Long departmentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    /**
     * Sum of expenses for a department filtered by status list within a given month/year.
     */
    @Query("""
            SELECT COALESCE(SUM(e.amount), 0) FROM Expense e
            WHERE e.user.department.id = :departmentId
              AND e.status IN :statuses
              AND FUNCTION('MONTH', e.expenseDate) = :month
              AND FUNCTION('YEAR', e.expenseDate) = :year
            """)
    BigDecimal sumByDepartmentAndStatusInAndMonthAndYear(
            @Param("departmentId") Long departmentId,
            @Param("statuses") List<ExpenseStatus> statuses,
            @Param("month") int month,
            @Param("year") int year
    );
}
