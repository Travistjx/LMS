package com.app.lms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Entity
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loan_id;

    private LocalDateTime loanDateTime;
    private LocalDateTime dueDateTime;

    private LocalDateTime returnedDateTime;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    @Column(name = "fine_amount")
    private Double fineAmount; // Added to store the fine amount

    private Long daysOverdue;

    private boolean deleted;

    // Constructors, getters, setters
    public Loan() {
        this.status = LoanStatus.ACTIVE;
    }
}

