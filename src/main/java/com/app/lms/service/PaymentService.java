package com.app.lms.service;

import com.app.lms.entity.IdType;
import com.app.lms.entity.Member;
import com.app.lms.entity.Payment;
import com.app.lms.web.FineDto;
import com.app.lms.web.PaymentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PaymentService {
    void makePayment (Collection<FineDto> fineDtos, Double fineAmount, String paymentMethod, String email);
    Page<PaymentDto> searchPayments(String query, Pageable pageable, Optional<Long> id, IdType idType,
                                    String statusFilter, String searchBy, String sort, String order);
    Page<PaymentDto> findPaginated(int pageNo, int pageSize, Optional<Long> id, IdType idType);
    PaymentDto getPaymentsByUser (Long member_id);

    PaymentDto convertEntityToDto(Payment payment);
}
