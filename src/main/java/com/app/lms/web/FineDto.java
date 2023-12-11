package com.app.lms.web;

import com.app.lms.entity.FineStatus;
import com.app.lms.entity.Loan;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class FineDto {
    private Long fine_id;
    private LoanDto loan;

    private Double amount;
    private LocalDateTime dateTimeOfFine;

    private FineStatus status;

    public FineDto() {
        this.status = FineStatus.UNPAID;
    }
}
