package com.app.lms.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Fine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fine_id;
    @OneToOne
    @JoinColumn(name = "loan_id") // Name of the foreign key column in the Fine table
    private Loan loan;
    private Double amount;
    private LocalDateTime dateTimeOfFine;

    @Enumerated(EnumType.STRING)
    private FineStatus status;

    public Fine() {
        this.status = FineStatus.UNPAID;
    }


    public Fine(Long fine_id, Loan loan, Double amount, LocalDateTime dateTimeOfFine, FineStatus status) {
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

    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
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
