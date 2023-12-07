package com.app.lms.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    // Constructors, getters, setters
    public Loan() {
        this.status = LoanStatus.ACTIVE;
    }

    public Loan(Long loan_id, LocalDateTime loanDateTime, LocalDateTime dueDateTime, LocalDateTime returnedDateTime,
                Book book, Member member, LoanStatus status, double fineAmount, Long daysOverdue) {
        this.loan_id = loan_id;
        this.loanDateTime = loanDateTime;
        this.dueDateTime = dueDateTime;
        this.returnedDateTime = returnedDateTime;
        this.book = book;
        this.member = member;
        this.status = status;
        this.fineAmount = fineAmount;
        this.daysOverdue = daysOverdue;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    public Long getLoan_id() {
        return loan_id;
    }

    public void setLoan_id(Long loan_id) {
        this.loan_id = loan_id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public LocalDateTime getLoanDateTime() {
        return loanDateTime;
    }

    public void setLoanDateTime(LocalDateTime loanDateTime) {
        this.loanDateTime = loanDateTime;
    }

    public LocalDateTime getDueDateTime() {
        return dueDateTime;
    }

    public void setDueDateTime(LocalDateTime dueDateTime) {
        this.dueDateTime = dueDateTime;
    }

    public LocalDateTime getReturnedDateTime() {
        return returnedDateTime;
    }

    public void setReturnedDateTime(LocalDateTime returnedDateTime) {
        this.returnedDateTime = returnedDateTime;
    }

    public Double getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(Double fineAmount) {
        this.fineAmount = fineAmount;
    }

    public Long getDaysOverdue() {
        return daysOverdue;
    }

    public void setDaysOverdue(Long daysOverdue) {
        this.daysOverdue = daysOverdue;
    }
}

