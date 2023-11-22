package com.app.lms.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Collection;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long payment_id;

    @Column(name = "payment_amount")
    private Double paymentAmount;
    private LocalDateTime paymentDateTime;
    private String paymentMethod;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    private String invoiceNumber;
    private String transactionReference;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "payment_fines",
            joinColumns = @JoinColumn(
                    name = "payment_id", referencedColumnName = "payment_id"),
            inverseJoinColumns = @JoinColumn(
                    name = "fine_id", referencedColumnName = "fine_id"))
    private Collection<Fine> fines;

    public Payment() {
        this.status = PaymentStatus.PENDING;
    }

    public Payment(Long payment_id, Double paymentAmount, LocalDateTime paymentDateTime, String paymentMethod,
                   PaymentStatus status, String invoiceNumber, String transactionReference, Collection <Fine> fines,
                   Member member) {
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

    public Collection<Fine> getFines() {
        return fines;
    }

    public void setFines(Collection<Fine> fines) {
        this.fines = fines;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }
}
