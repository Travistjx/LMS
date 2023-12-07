package com.app.lms.service.serviceimpl;

import com.app.lms.entity.*;
import com.app.lms.repository.FineRepository;
import com.app.lms.repository.LoanRepository;
import com.app.lms.repository.MemberRepository;
import com.app.lms.repository.PaymentRepository;
import com.app.lms.service.FineService;
import com.app.lms.service.LoanService;
import com.app.lms.service.MemberService;
import com.app.lms.service.PaymentService;
import com.app.lms.web.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final FineRepository fineRepository;
    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;
    private final FineService fineService;
    private final MemberService memberService;

    public PaymentServiceImpl(FineRepository fineRepository, MemberRepository memberRepository,
                              PaymentRepository paymentRepository, FineService fineService, MemberService memberService) {
        this.fineRepository = fineRepository;
        this.memberRepository = memberRepository;
        this.paymentRepository = paymentRepository;
        this.fineService = fineService;
        this.memberService = memberService;
    }

    // Make payment
    @Override
    public void makePayment (Collection<FineDto> fineDtos, Double fineAmount, String paymentMethod, String email){
        Payment payment = new Payment();
        List<Fine> finesList = fineDtos.stream()
                .map(fineDto -> {
                    Fine fine = fineRepository.findById(fineDto.getFine_id())
                             .orElseThrow(() -> new EntityNotFoundException("Fine not found with ID: " + fineDto.getFine_id()));
                    fine.setStatus(FineStatus.PAID); //Update Fine status

                    return fine;
                })
                .collect(Collectors.toList());

        payment.setFines(finesList);
        payment.setPaymentAmount(fineAmount);
        payment.setStatus(PaymentStatus.SUCCESSFUL);
        payment.setPaymentDateTime(LocalDateTime.now());
        Member member = memberRepository.findByEmail(email);
        payment.setMember(member);

        // find latest payment
        Payment latestPayment = paymentRepository.findFirstByOrderByPaymentDateTimeDesc();
        String formattedInvoiceNumber = "";

        if (latestPayment != null) {
            int invoiceNumber = Integer.parseInt(latestPayment.getInvoiceNumber().substring(4));
            formattedInvoiceNumber = String.format("INV-%05d", invoiceNumber + 1);
        } else {
            formattedInvoiceNumber = String.format("INV-%05d", 0);
        }
        payment.setInvoiceNumber(formattedInvoiceNumber);
        payment.setPaymentMethod(paymentMethod);
        payment.setTransactionReference("XXXXXXXXXXX");

        paymentRepository.save(payment);
    }

    // Get the newest payment information based on Member Id
    @Override
    public PaymentDto getPaymentsByUser (Long member_id){
        List <Payment> paymentList = paymentRepository.findAll();
        List<PaymentDto> selectedPayments = paymentList.stream()
                .filter(payment -> payment.getFines().stream().findFirst()
                        .get().getLoan().getMember().getMember_id().equals(member_id))
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());

        if (!selectedPayments.isEmpty()) {
            return selectedPayments.get(selectedPayments.size() - 1);
        }
        else {
            return null;
        }
    }

    // Retrieve pages of payment logs based on search filter
    @Override
    public Page<PaymentDto> searchPayments(String query, Pageable pageable, Optional<Long> id, IdType idType,
                                           String statusFilter, String searchBy, String sort, String order) {
        Page<Payment> selectedPayment = null;

        // set status
        PaymentStatus status = PaymentStatus.FAILED;
        if (statusFilter.equals("ALL")){
            status = null;
        }
        else if (statusFilter.equals("SUCCESSFUL")){
            status = PaymentStatus.SUCCESSFUL;
        }
        else if (statusFilter.equals("FAILED")){
            status = PaymentStatus.FAILED;
        }
        else if (statusFilter.equals("PENDING")){
            status = PaymentStatus.PENDING;
        }

        // set sort order
        Sort.Direction direction = Sort.Direction.ASC;

        if (order.equals("desc")){
            direction = Sort.Direction.DESC;
        }

        // set sort option
        Sort sortable = Sort.by(direction, "payment_id"); // Default sorting by title

        if (sort != null) {
            switch (sort) {
                case "fineId":
                    sortable = Sort.by(direction, "a.fine_id");
                    break;
                case "invoiceNumber":
                    sortable = Sort.by(direction, "invoiceNumber");
                    break;
                case "transactionRef":
                    sortable = Sort.by(direction, "transactionReference");
                    break;
                case "title":
                    sortable = Sort.by(direction, "book.title");
                    break;
                case "paymentMethod":
                    sortable = Sort.by(direction, "paymentMethod");
                    break;
                case "amount":
                    sortable = Sort.by(direction, "paymentAmount");
                    break;
                case "paymentDateTime":
                    sortable = Sort.by(direction, "paymentDateTime");
                    break;
                case "memberId":
                    sortable = Sort.by(direction, "member.member_id");
                    break;
                case "name":
                    sortable = Sort.by(direction, "member.firstName");
                    break;
            }
        }

        if (id.isPresent()){
            Long selectedId = id.get();

            if (idType == IdType.MEMBER_ID) {
                if (searchBy.equals("any")){
                    selectedPayment = paymentRepository.searchPaymentsByMemberWithStatusAndByAny(query,
                            selectedId, status, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));
                }
                else { // if search by member and searchBy != "any"
                    selectedPayment = paymentRepository.searchPaymentsByMemberWithStatusAndNotAny(query,
                            selectedId, status, searchBy, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));
                }
            }
        }
        else {
            if (searchBy.equals("any")){
                selectedPayment = paymentRepository.searchPaymentsWithStatusAndByAny(query, status, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));
            }
            else { // if searchBy != "any"
                selectedPayment = paymentRepository.searchPaymentsWithStatusAndNotAny(query, status, searchBy, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));
            }
        }

        List <PaymentDto> selectedPaymentsDto = selectedPayment.getContent().stream().map(this::convertEntityToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(selectedPaymentsDto, pageable, selectedPayment.getTotalElements());
    }

    // Retrieves pages of all payments made
    @Override
    public Page<PaymentDto> findPaginated(int pageNo, int pageSize, Optional<Long> id, IdType idType) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<Payment> paymentPage = this.paymentRepository.findAll(pageable);

        // if finding by member or book
        if (id.isPresent()){
            Long paymentId = id.get();

            if (idType == IdType.MEMBER_ID){
                Member member = memberRepository.findById(paymentId).get();
                paymentPage = this.paymentRepository.findAllByMember(member, pageable);
            }

        }

        List <PaymentDto> selectedPaymentsDto = paymentPage.getContent().stream().map(this::convertEntityToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(selectedPaymentsDto, pageable, paymentPage.getTotalElements());
    }

    // Convert payment entity to paymentDto
    public PaymentDto convertEntityToDto(Payment payment) {
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setFines(payment.getFines().stream().map(fineService::convertEntityToDto).collect(Collectors.toList()));
        paymentDto.setPayment_id(payment.getPayment_id());
        paymentDto.setStatus(payment.getStatus());
        paymentDto.setInvoiceNumber(payment.getInvoiceNumber());
        paymentDto.setTransactionReference(payment.getTransactionReference());

        MemberDto memberDto = memberService.convertEntityToDto(payment.getMember());
        paymentDto.setMember(memberDto);
        paymentDto.setPaymentAmount(payment.getPaymentAmount());
        paymentDto.setPaymentDateTime(payment.getPaymentDateTime());
        paymentDto.setPaymentMethod(payment.getPaymentMethod());

        return paymentDto;
    }
}
