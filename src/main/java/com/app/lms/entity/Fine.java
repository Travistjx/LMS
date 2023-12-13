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
public class Fine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fine_id;
    @OneToOne
    @JoinColumn(name = "loan_id") // Name of the foreign key column in the Fine table
    private Loan loan;
    private Double amount;
    private LocalDateTime dateTimeOfFine;

    private boolean deleted;

    @Enumerated(EnumType.STRING)
    private FineStatus status;

    public Fine() {
        this.status = FineStatus.UNPAID;
    }
}
