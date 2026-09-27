package com.example.expense_tracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import com.example.expense_tracker.enums.ExpenseStatus;


@Entity
@Table(name = "expenses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Expense {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private BigDecimal amount;


    private String description;


    private LocalDate expenseDate;


    private String receiptUrl;


    @Enumerated(EnumType.STRING)
    private ExpenseStatus status;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    @ManyToOne
    @JoinColumn(name = "category_id")
    private ExpenseCategory category;


    @OneToMany(mappedBy = "expense")
    private List<ExpenseApproval> approvals;

}