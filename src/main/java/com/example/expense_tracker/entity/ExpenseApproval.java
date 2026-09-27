package com.example.expense_tracker.entity;

import com.example.expense_tracker.enums.ApprovalStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;



@Entity
@Table(name = "expense_approvals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseApproval {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Enumerated(EnumType.STRING)
    private ApprovalStatus action;


    private String comment;


    private LocalDateTime approvedAt;


    @ManyToOne
    @JoinColumn(name = "expense_id")
    private Expense expense;


    @ManyToOne
    @JoinColumn(name = "approver_id")
    private User approver;

}