package com.app.lms.web;
import com.app.lms.entity.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
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
}

