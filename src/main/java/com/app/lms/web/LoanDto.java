package com.app.lms.web;

import com.app.lms.entity.Book;
import com.app.lms.entity.Gender;
import com.app.lms.entity.LoanStatus;
import com.app.lms.entity.Member;
import jakarta.persistence.Column;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LoanDto {
    private Long loan_id;

    private LocalDateTime loanDateTime;
    private LocalDateTime dueDateTime;

    private LocalDateTime returnedDateTime;
    private LoanStatus status;

    private BookDto book;

    private MemberDto member;

    private Double fineAmount; // Added to store the fine amount

    private Long daysOverdue;

    // Constructors, getters, setters
    public LoanDto() {
        this.status = LoanStatus.ACTIVE;
    }

    public LoanDto(Long loan_id, LocalDateTime loanDateTime, LocalDateTime dueDateTime, LocalDateTime returnedDateTime,
                   LoanStatus status, BookDto book, MemberDto member, double fineAmount, Long daysOverdue) {
        this.loan_id = loan_id;
        this.loanDateTime = loanDateTime;
        this.dueDateTime = dueDateTime;
        this.returnedDateTime = returnedDateTime;
        this.status = status;
        this.book = book;
        this.member = member;
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

    public LocalDateTime getReturnedDateTime() {
        return returnedDateTime;
    }

    public void setReturnedDateTime(LocalDateTime returnedDateTime) {
        this.returnedDateTime = returnedDateTime;
    }

    public BookDto getBook() {
        return book;
    }

    public void setBook(BookDto book) {
        this.book = book;
    }

    public MemberDto getMember() {
        return member;
    }

    public void setMember(MemberDto member) {
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

