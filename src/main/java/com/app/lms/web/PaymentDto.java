package com.app.lms.web;

import com.app.lms.entity.Fine;
import com.app.lms.entity.Member;
import com.app.lms.entity.PaymentStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Collection;

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

    public PaymentDto() {

    }

    public PaymentDto(Long payment_id, Double paymentAmount, LocalDateTime paymentDateTime, String paymentMethod,
                      PaymentStatus status, String invoiceNumber, String transactionReference, Collection<FineDto> fines,
                      MemberDto member) {
        this.payment_id = payment_id;
        this.paymentAmount = paymentAmount;
        this.paymentDateTime = paymentDateTime;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.invoiceNumber = invoiceNumber;
        this.transactionReference = transactionReference;
        this.fines = fines;
        this.member = member;
    }

    public Double getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(Double paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public LocalDateTime getPaymentDateTime() {
        return paymentDateTime;
    }

    public void setPaymentDateTime(LocalDateTime paymentDateTime) {
        this.paymentDateTime = paymentDateTime;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getTransactionReference() {
        return transactionReference;
    }


    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public Long getPayment_id() {
        return payment_id;
    }

    public void setPayment_id(Long payment_id) {
        this.payment_id = payment_id;
    }

    public Collection<FineDto> getFines() {
        return fines;
    }

    public void setFines(Collection<FineDto> fines) {
        this.fines = fines;
    }

    public MemberDto getMember() {
        return member;
    }

    public void setMember(MemberDto member) {
        this.member = member;
    }
}
