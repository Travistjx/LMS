package com.app.lms.web;

import com.app.lms.entity.FineStatus;
import com.app.lms.entity.Loan;
import jakarta.persistence.*;

import java.time.LocalDateTime;

public class FineDto {
    private Long fine_id;
    private LoanDto loan;

    private Double amount;
    private LocalDateTime dateTimeOfFine;

    private FineStatus status;

    public FineDto() {
        this.status = FineStatus.UNPAID;
    }

    public FineDto(Long fine_id, LoanDto loan, Double amount, LocalDateTime dateTimeOfFine,
                   FineStatus status) {
        this.fine_id = fine_id;
        this.loan = loan;
        this.amount = amount;
        this.dateTimeOfFine = dateTimeOfFine;
        this.status = status;
    }

    public Long getFine_id() {
        return fine_id;
    }

    public void setFine_id(Long fine_id) {
        this.fine_id = fine_id;
    }

    public LoanDto getLoan() {
        return loan;
    }

    public void setLoan(LoanDto loan) {
        this.loan = loan;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getDateTimeOfFine() {
        return dateTimeOfFine;
    }

    public void setDateTimeOfFine(LocalDateTime dateTimeOfFine) {
        this.dateTimeOfFine = dateTimeOfFine;
    }


    public FineStatus getStatus() {
        return status;
    }

    public void setStatus(FineStatus status) {
        this.status = status;
    }
}
