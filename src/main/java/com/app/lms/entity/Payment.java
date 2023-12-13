package com.app.lms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
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
    private boolean deleted;

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
}
