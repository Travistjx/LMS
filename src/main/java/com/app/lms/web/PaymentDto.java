package com.app.lms.web;

import com.app.lms.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {

    private Long payment_id;
    private Double paymentAmount;
    private LocalDateTime paymentDateTime;
    private String paymentMethod;
    private PaymentStatus status;
    private String invoiceNumber;
    private String transactionReference;
    private MemberDto member;
    private Collection<FineDto> fines;
}
