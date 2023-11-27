package com.app.lms.repository;

import com.app.lms.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private FineRepository fineRepository;

    @Autowired
    private LoanRepository loanRepository;


    @Test
    //find latest payment
    void itShouldFindFirstByOrderByPaymentDateTimeDesc() {
        Payment payment = new Payment();
        payment.setPaymentMethod("Credit Card");
        payment.setPaymentAmount(15.0);
        payment.setPaymentDateTime(LocalDateTime.now());
        payment.setTransactionReference("XXX");
        payment.setInvoiceNumber("INV-000");
        paymentRepository.save(payment);

        Payment paymentTwo = new Payment();
        paymentTwo.setPaymentMethod("Credit Card");
        paymentTwo.setPaymentAmount(18.0);
        paymentTwo.setPaymentDateTime(LocalDateTime.now());
        paymentTwo.setTransactionReference("XXX");
        paymentTwo.setInvoiceNumber("INV-000");
        paymentRepository.save(paymentTwo);

        Payment latestPayment = paymentRepository.findFirstByOrderByPaymentDateTimeDesc();

        boolean exist = false;

        if (latestPayment != null && latestPayment.getPaymentAmount() == 18.0) exist  = true;

        assertThat(exist).isTrue();
    }

    @Test
        //find latest payment
    void itShouldNotFindFirstByOrderByPaymentDateTimeDesc() {
        Payment latestPayment = paymentRepository.findFirstByOrderByPaymentDateTimeDesc();

        boolean exist = true;

        if (latestPayment == null) exist  = false;

        assertThat(exist).isFalse();
    }

    @Test
    void itShouldFindAllByMember() {
        //create member obj
        Member member = new Member();
        member.setEmail("georgeng@gmail.com");
        memberRepository.save(member);

        Fine fine = new Fine();
        fine.setDateTimeOfFine(LocalDateTime.of(2023, 10, 30, 14, 30));
        fine.setStatus(FineStatus.PAID);
        fine.setAmount(15.0);
        fineRepository.save(fine);

        Payment payment = new Payment();
        payment.setPaymentMethod("Credit Card");
        payment.setPaymentAmount(15.0);
        payment.setPaymentDateTime(LocalDateTime.now());
        payment.setTransactionReference("XXX");
        payment.setInvoiceNumber("INV-000");
        payment.setStatus(PaymentStatus.SUCCESSFUL);
        payment.setMember(member);
        payment.setFines(List.of(fine));

        paymentRepository.save(payment);

        int pageSize = 5;

        Pageable pageable = PageRequest.of(0, pageSize);

        Page <Payment> paymentList = paymentRepository.findAllByMember(member, pageable);

        boolean exist = false;

        if (paymentList.hasContent()) exist  = true;

        assertThat(exist).isTrue();
    }

    @Test
    void itShouldNotFindAllByMember() {
        //create member obj
        Member member = new Member();
        member.setEmail("georgeng@gmail.com");
        memberRepository.save(member);

        int pageSize = 5;

        Pageable pageable = PageRequest.of(0, pageSize);

        Page <Payment> paymentList = paymentRepository.findAllByMember(member, pageable);

        boolean exist = true;

        if (!paymentList.hasContent()) exist  = false;

        assertThat(exist).isFalse();
    }

    @Test
    void itShouldSearchPaymentsWithStatusAndByAny() {
        Member member = new Member();
        member.setEmail("georgeng@gmail.com");
        memberRepository.save(member);

        Fine fine = new Fine();
        fine.setDateTimeOfFine(LocalDateTime.of(2023, 10, 30, 14, 30));
        fine.setStatus(FineStatus.PAID);
        fine.setAmount(15.0);
        fineRepository.save(fine);

        Payment payment = new Payment();
        payment.setPaymentMethod("Credit Card");
        payment.setPaymentAmount(15.0);
        payment.setPaymentDateTime(LocalDateTime.now());
        payment.setTransactionReference("XXX");
        payment.setInvoiceNumber("INV-000");
        payment.setStatus(PaymentStatus.SUCCESSFUL);
        payment.setMember(member);
        payment.setFines(List.of(fine));

        paymentRepository.save(payment);

        int pageSize = 5;
        Pageable pageable = PageRequest.of(0, pageSize);
        String query = "Credit";
        PaymentStatus status = PaymentStatus.SUCCESSFUL;
        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "payment_id");

        Page <Payment> paymentList = paymentRepository.searchPaymentsWithStatusAndByAny(query, status, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));

        boolean exist = false;

        if (paymentList.hasContent()) exist  = true;

        assertThat(exist).isTrue();
    }

    @Test
    void itShouldNotSearchPaymentsWithStatusAndByAny() {
        int pageSize = 5;
        Pageable pageable = PageRequest.of(0, pageSize);
        String query = "Credit";
        PaymentStatus status = PaymentStatus.SUCCESSFUL;
        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "payment_id");

        Page <Payment> paymentList = paymentRepository.searchPaymentsWithStatusAndByAny(query, status, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));

        boolean exist = true;

        if (!paymentList.hasContent()) exist  = false;

        assertThat(exist).isFalse();
    }

    @Test
    void itShouldSearchPaymentsWithStatusAndNotAny() {
        Member member = new Member();
        member.setEmail("georgeng@gmail.com");
        memberRepository.save(member);

        Fine fine = new Fine();
        fine.setDateTimeOfFine(LocalDateTime.of(2023, 10, 30, 14, 30));
        fine.setStatus(FineStatus.PAID);
        fine.setAmount(15.0);
        fineRepository.save(fine);

        Payment payment = new Payment();
        payment.setPaymentMethod("Credit Card");
        payment.setPaymentAmount(15.0);
        payment.setPaymentDateTime(LocalDateTime.now());
        payment.setTransactionReference("XXX");
        payment.setInvoiceNumber("INV-000");
        payment.setStatus(PaymentStatus.SUCCESSFUL);
        payment.setMember(member);
        payment.setFines(List.of(fine));

        paymentRepository.save(payment);

        int pageSize = 5;
        Pageable pageable = PageRequest.of(0, pageSize);
        String query = "Credit";
        String searchBy = "paymentType";
        PaymentStatus status = PaymentStatus.SUCCESSFUL;
        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "payment_id");

        Page <Payment> paymentList = paymentRepository.searchPaymentsWithStatusAndNotAny(query, status, searchBy,PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));

        boolean exist = false;

        if (paymentList.hasContent()) exist  = true;

        assertThat(exist).isTrue();
    }

    @Test
    void itShouldNotSearchPaymentsWithStatusAndNotAny() {
        int pageSize = 5;
        Pageable pageable = PageRequest.of(0, pageSize);
        String query = "Credit";
        String searchBy = "paymentType";
        PaymentStatus status = PaymentStatus.SUCCESSFUL;
        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "payment_id");

        Page <Payment> paymentList = paymentRepository.searchPaymentsWithStatusAndNotAny(query, status, searchBy,PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));

        boolean exist = true;

        if (!paymentList.hasContent()) exist  = false;

        assertThat(exist).isFalse();
    }

    @Test
    void itShouldSearchPaymentsByMemberWithStatusAndByAny() {
        Member member = new Member();
        member.setEmail("georgeng@gmail.com");
        memberRepository.save(member);

        Fine fine = new Fine();
        fine.setDateTimeOfFine(LocalDateTime.of(2023, 10, 30, 14, 30));
        fine.setStatus(FineStatus.PAID);
        fine.setAmount(15.0);
        fineRepository.save(fine);

        Payment payment = new Payment();
        payment.setPaymentMethod("Credit Card");
        payment.setPaymentAmount(15.0);
        payment.setPaymentDateTime(LocalDateTime.now());
        payment.setTransactionReference("XXX");
        payment.setInvoiceNumber("INV-000");
        payment.setStatus(PaymentStatus.SUCCESSFUL);
        payment.setMember(member);
        payment.setFines(List.of(fine));

        paymentRepository.save(payment);

        int pageSize = 5;
        Pageable pageable = PageRequest.of(0, pageSize);
        String query = "Credit";
        String searchBy = "paymentType";
        PaymentStatus status = PaymentStatus.SUCCESSFUL;
        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "payment_id");

        Page <Payment> paymentList = paymentRepository.searchPaymentsByMemberWithStatusAndByAny(query, member.getMember_id(), status,PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));

        boolean exist = false;

        if (paymentList.hasContent()) exist  = true;

        assertThat(exist).isTrue();
    }

    @Test
    void itShouldNotSearchPaymentsByMemberWithStatusAndByAny() {
        Member member = new Member();
        member.setEmail("georgeng@gmail.com");
        memberRepository.save(member);

        int pageSize = 5;
        Pageable pageable = PageRequest.of(0, pageSize);
        String query = "Credit";
        String searchBy = "paymentType";
        PaymentStatus status = PaymentStatus.SUCCESSFUL;
        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "payment_id");

        Page <Payment> paymentList = paymentRepository.searchPaymentsByMemberWithStatusAndByAny(query, member.getMember_id(), status,PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));

        boolean exist = true;

        if (!paymentList.hasContent()) exist  = false;

        assertThat(exist).isFalse();
    }

    @Test
    void itShouldSearchPaymentsByMemberWithStatusAndNotAny() {
        Member member = new Member();
        member.setEmail("georgeng@gmail.com");
        memberRepository.save(member);

        Fine fine = new Fine();
        fine.setDateTimeOfFine(LocalDateTime.of(2023, 10, 30, 14, 30));
        fine.setStatus(FineStatus.PAID);
        fine.setAmount(15.0);
        fineRepository.save(fine);

        Payment payment = new Payment();
        payment.setPaymentMethod("Credit Card");
        payment.setPaymentAmount(15.0);
        payment.setPaymentDateTime(LocalDateTime.now());
        payment.setTransactionReference("XXX");
        payment.setInvoiceNumber("INV-000");
        payment.setStatus(PaymentStatus.SUCCESSFUL);
        payment.setMember(member);
        payment.setFines(List.of(fine));

        paymentRepository.save(payment);

        int pageSize = 5;
        Pageable pageable = PageRequest.of(0, pageSize);
        String query = "Credit";
        String searchBy = "paymentType";
        PaymentStatus status = PaymentStatus.SUCCESSFUL;
        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "payment_id");

        Page <Payment> paymentList = paymentRepository.searchPaymentsByMemberWithStatusAndNotAny(query, member.getMember_id(), status , searchBy, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));

        boolean exist = false;

        if (paymentList.hasContent()) exist  = true;

        assertThat(exist).isTrue();
    }

    @Test
    void itShouldNotSearchPaymentsByMemberWithStatusAndNotAny() {
        Member member = new Member();
        member.setEmail("georgeng@gmail.com");
        memberRepository.save(member);

        int pageSize = 5;
        Pageable pageable = PageRequest.of(0, pageSize);
        String query = "Credit";
        String searchBy = "paymentType";
        PaymentStatus status = PaymentStatus.SUCCESSFUL;
        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "payment_id");

        Page <Payment> paymentList = paymentRepository.searchPaymentsByMemberWithStatusAndNotAny(query, member.getMember_id(), status , searchBy, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));

        boolean exist = true;

        if (!paymentList.hasContent()) exist  = false;

        assertThat(exist).isFalse();
    }
}