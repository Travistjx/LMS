package com.app.lms.repository;

import com.app.lms.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findFirstByOrderByPaymentDateTimeDesc();
    Page<Payment> findAllByMember(Member member, Pageable pageable);

    @Query("SELECT l FROM Payment l " +
            "LEFT JOIN l.fines a " +
            "WHERE (l.payment_id LIKE %:query% OR " +
            "l.paymentAmount LIKE %:query% OR " +
            "l.paymentDateTime LIKE %:query% OR " +
            "l.paymentMethod LIKE %:query% OR " +
            "l.transactionReference LIKE %:query% OR " +
            "l.invoiceNumber LIKE %:query% OR " +
            "l.member.lastName LIKE %:query% OR " +
            "l.member.firstName LIKE %:query% OR " +
            "l.member.member_id LIKE %:query% OR " +
            "a.fine_id LIKE %:query%)" +
            "AND (:statusFilter IS NULL OR l.status = :statusFilter)" +
            "GROUP BY l.payment_id")
    Page<Payment> searchPaymentsWithStatusAndByAny(@Param("query") String query,
                                             @Param("statusFilter") PaymentStatus statusFilter,
                                             Pageable pageable);

    @Query("SELECT l FROM Payment l " +
            "LEFT JOIN l.fines a " +
            "WHERE " +
            "((:searchBy = 'paymentId' AND l.payment_id LIKE %:query%) OR " +
            "(:searchBy = 'invoiceNumber' AND l.invoiceNumber LIKE %:query%) OR " +
            "(:searchBy = 'paymentAmount' AND l.paymentAmount LIKE %:query%) OR " +
            "(:searchBy = 'transactionReference' AND l.transactionReference LIKE %:query%) OR " +
            "(:searchBy = 'paymentDateTime' AND l.paymentDateTime LIKE %:query%) OR " +
            "((:searchBy = 'name' AND l.member.firstName LIKE %:query%) OR " +
            "(:searchBy = 'name' AND l.member.lastName LIKE %:query%)) OR " +
            "(:searchBy = 'memberId' AND l.member.member_id LIKE %:query%) OR " +
            "(:searchBy = 'fineId' AND a.fine_id LIKE %:query%)) " +
            "AND (:statusFilter IS NULL OR l.status = :statusFilter)"  +
            "GROUP BY l.payment_id")
    Page<Payment> searchPaymentsWithStatusAndNotAny(@Param("query") String query,
                                              @Param("statusFilter") PaymentStatus statusFilter,
                                              @Param("searchBy") String searchBy,
                                              Pageable pageable);

    @Query("SELECT l FROM Payment l " +
            "LEFT JOIN l.fines a " +
            "WHERE (l.payment_id LIKE %:query% OR " +
            "l.paymentAmount LIKE %:query% OR " +
            "l.paymentDateTime LIKE %:query% OR " +
            "l.paymentMethod LIKE %:query% OR " +
            "l.invoiceNumber LIKE %:query% OR " +
            "l.transactionReference LIKE %:query% OR " +
            "a.fine_id LIKE %:query%) AND " +
            "l.member.member_id = :member_id AND " +
            "(:statusFilter IS NULL OR l.status = :statusFilter)"  +
            "GROUP BY l.payment_id")
    Page<Payment> searchPaymentsByMemberWithStatusAndByAny(@Param("query") String query,
                                                     @Param("member_id") Long member_id,
                                                     @Param("statusFilter") PaymentStatus statusFilter,
                                                     Pageable pageable);

    @Query("SELECT l FROM Payment l " +
            "LEFT JOIN l.fines a " +
            "WHERE " +
            "((:searchBy = 'paymentId' AND l.payment_id LIKE %:query%) OR " +
            "(:searchBy = 'invoiceNumber' AND l.invoiceNumber LIKE %:query%) OR " +
            "(:searchBy = 'paymentAmount' AND l.paymentAmount LIKE %:query%) OR " +
            "(:searchBy = 'transactionReference' AND l.transactionReference LIKE %:query%) OR " +
            "(:searchBy = 'paymentDateTime' AND l.paymentDateTime LIKE %:query%) OR " +
            "(:searchBy = 'fineId' AND a.fine_id LIKE %:query%)) AND " +
            "(l.member.member_id = :member_id) " +
            "AND (:statusFilter IS NULL OR l.status = :statusFilter)"  +
            "GROUP BY l.payment_id")
    Page<Payment> searchPaymentsByMemberWithStatusAndNotAny(@Param("query") String query,
                                                      @Param("member_id") Long member_id,
                                                      @Param("statusFilter") PaymentStatus statusFilter,
                                                      @Param("searchBy") String searchBy,
                                                      Pageable pageable);
}
