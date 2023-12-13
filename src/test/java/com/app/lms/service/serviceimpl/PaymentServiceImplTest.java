package com.app.lms.service.serviceimpl;

import com.app.lms.entity.*;
import com.app.lms.repository.FineRepository;
import com.app.lms.repository.MemberRepository;
import com.app.lms.repository.PaymentRepository;
import com.app.lms.service.FineService;
import com.app.lms.service.MemberService;
import com.app.lms.service.PaymentService;
import com.app.lms.web.FineDto;
import com.app.lms.web.MemberDto;
import com.app.lms.web.PaymentDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private FineRepository fineRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private FineService fineService;

    @Mock
    private MemberService memberService;

    private PaymentService paymentService;

    @BeforeEach
    void setup (){
        paymentService = new PaymentServiceImpl(fineRepository, memberRepository, paymentRepository,
                fineService, memberService);
    }


    @Test
    void makePayment() {
        FineDto fineDto = new FineDto();
        fineDto.setFine_id(1L);
        FineDto fineDto2 = new FineDto();
        fineDto2.setFine_id(2L);
        Double fineAmount = 15.0;
        String paymentMethod = "Credit Card";
        Member member = new Member();
        member.setEmail("georgeng@gmail.com");

        Fine fine = new Fine();
        fine.setFine_id(1L);
        Fine fine2 = new Fine();
        fine2.setFine_id(2L);

        Payment payment = new Payment();
        payment.setPaymentDateTime(LocalDateTime.of(2023, 10, 30, 14, 30));
        when(fineRepository.findById(1L)).thenReturn(Optional.of(fine));
        when(fineRepository.findById(2L)).thenReturn(Optional.of(fine2));

        when(memberRepository.findByEmail("georgeng@gmail.com")).thenReturn(member);
        when(paymentRepository.findFirstByOrderByPaymentDateTimeDesc()).thenReturn(null);

        paymentService.makePayment(Arrays.asList(fineDto, fineDto2), fineAmount,
                paymentMethod, "georgeng@gmail.com");

        ArgumentCaptor<Payment> paymentArgumentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentArgumentCaptor.capture());
        Payment capturedPayment = paymentArgumentCaptor.getValue();

        assertThat(capturedPayment.getPaymentAmount()).isEqualTo(fineAmount);
        assertThat(capturedPayment.getPaymentMethod()).isEqualTo(paymentMethod);

    }

    @Test
    void getPaymentsByUser() {
        Payment payment = new Payment();
        payment.setPaymentAmount(1.0);
        payment.setPaymentMethod("Credit");
        payment.setStatus(PaymentStatus.FAILED);
        Member member = new Member();
        member.setMember_id(1L);
        payment.setMember(member);
        Loan loan = new Loan();
        Fine fine = new Fine();
        Collection<Fine> fineList = new ArrayList<>();
        loan.setMember(member);
        fine.setLoan(loan);
        fineList.add(fine);
        payment.setFines(fineList);


        when(paymentRepository.findAll()).thenReturn(List.of(payment));
        when(fineService.convertEntityToDto(any(Fine.class))).thenReturn(new FineDto());
        when(memberService.convertEntityToDto(any(Member.class))).thenReturn(new MemberDto());

        PaymentDto paymentDto = paymentService.getPaymentsByUser(1L);

        assertThat(paymentDto.getPaymentAmount()).isEqualTo(payment.getPaymentAmount());
        assertThat(paymentDto.getPaymentMethod()).isEqualTo(payment.getPaymentMethod());
        assertThat(paymentDto.getStatus()).isEqualTo(payment.getStatus());
    }

    @Test
    void searchPayments() {
        int pageSize = 5;
        int pageNo = 1;
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

        String query = "1";
        String searchBy = "paymentAmount";
        String status = "SUCCESSFUL";
        PaymentStatus statusFilter = PaymentStatus.SUCCESSFUL;
        String sort = "paymentId";
        String order = "asc";

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "payment_id");

        when(paymentRepository.searchPaymentsWithStatusAndNotAny(query, statusFilter,searchBy,PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));


        paymentService.searchPayments(query, pageable, Optional.empty(), IdType.NONE, status, searchBy, sort ,order);
        verify(paymentRepository).searchPaymentsWithStatusAndNotAny(query, statusFilter,searchBy,PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));
    }

    @Test
    void findPaginated() {
        int pageSize = 5;
        int pageNo = 1;
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

        when(paymentRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        paymentService.findPaginated(pageNo, pageSize, Optional.empty(), IdType.NONE);


        verify(paymentRepository).findAll(pageable);
    }

    @Test
    void convertEntityToDto() {
        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.FAILED);
        payment.setPaymentMethod("Credit Card");
        payment.setPaymentDateTime(LocalDateTime.now());
        payment.setInvoiceNumber("INV-000");
        payment.setTransactionReference("XXX");
        Fine fine = new Fine();
        Collection <Fine> fineList = new ArrayList<>();
        fineList.add(fine);
        payment.setFines(fineList);
        Member member = new Member();
        payment.setMember(member);

        when(memberService.convertEntityToDto(any(Member.class))).thenReturn(new MemberDto());
        when(fineService.convertEntityToDto(any(Fine.class))).thenReturn(new FineDto());


        PaymentDto paymentDto = paymentService.convertEntityToDto(payment);

        assertThat(paymentDto.getPaymentAmount()).isEqualTo(payment.getPaymentAmount());
        assertThat(paymentDto.getStatus()).isEqualTo(payment.getStatus());
        assertThat(paymentDto.getPaymentMethod()).isEqualTo(payment.getPaymentMethod());
        assertThat(paymentDto.getPaymentDateTime()).isEqualTo(payment.getPaymentDateTime());
        assertThat(paymentDto.getInvoiceNumber()).isEqualTo(payment.getInvoiceNumber());
        assertThat(paymentDto.getTransactionReference()).isEqualTo(payment.getTransactionReference());
    }
}