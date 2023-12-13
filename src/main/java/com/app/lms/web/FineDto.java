package com.app.lms.web;

import com.app.lms.entity.FineStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
