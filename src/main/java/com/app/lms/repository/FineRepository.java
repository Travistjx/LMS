package com.app.lms.repository;

import com.app.lms.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FineRepository extends JpaRepository<Fine, Long> {
    List<Fine> findByLoan (Loan loan);

    @Query("SELECT l FROM Fine l " +
            "WHERE (l.loan.loan_id LIKE %:query% OR " +
            "l.fine_id LIKE %:query% OR " +
            "l.loan.member.firstName LIKE %:query% OR " +
            "l.loan.member.lastName LIKE %:query% OR " +
            "l.loan.member.member_id LIKE %:query% OR " +
            "l.status LIKE %:query% OR " +
            "l.loan.book.book_id LIKE %:query% OR " +
            "l.loan.book.title LIKE %:query% OR " +
            "l.amount LIKE %:query%) " +
            "AND (:statusFilter IS NULL OR l.status = :statusFilter)")
    Page<Fine> searchFinesWithStatusAndByAny(@Param("query") String query,
                                             @Param("statusFilter") FineStatus statusFilter,
                                             Pageable pageable);

    @Query("SELECT l FROM Fine l " +
            "WHERE " +
            "((:searchBy = 'loanId' AND l.loan.loan_id LIKE %:query%) OR " +
            "(:searchBy = 'fineId' AND l.fine_id LIKE %:query%) OR " +
            "((:searchBy = 'name' AND l.loan.member.firstName LIKE %:query%) OR " +
            "(:searchBy = 'name' AND l.loan.member.lastName LIKE %:query%)) OR " +
            "(:searchBy = 'memberId' AND l.loan.member.member_id LIKE %:query%) OR " +
            "(:searchBy = 'bookId' AND l.loan.book.book_id LIKE %:query%) OR " +
            "(:searchBy = 'title' AND l.loan.book.title LIKE %:query%) OR " +
            "(:searchBy = 'fine' AND l.amount LIKE %:query%)) " +
            "AND (:statusFilter IS NULL OR l.status = :statusFilter)")
    Page<Fine> searchFinesWithStatusAndNotAny(@Param("query") String query,
                                              @Param("statusFilter") FineStatus statusFilter,
                                              @Param("searchBy") String searchBy,
                                              Pageable pageable);

    @Query("SELECT l FROM Fine l " +
            "WHERE (l.loan.loan_id LIKE %:query% OR " +
            "l.fine_id LIKE %:query% OR " +
            "l.loan.book.book_id LIKE %:query% OR " +
            "l.loan.book.title LIKE %:query% OR " +
            "l.amount LIKE %:query%) AND " +
            "(l.loan.member.member_id = :member_id) " +
            "AND (:statusFilter IS NULL OR l.status = :statusFilter)")
    Page<Fine> searchFinesByMemberWithStatusAndByAny(@Param("query") String query,
                                             @Param("statusFilter") FineStatus statusFilter,
                                                     @Param("member_id") Long member_id,
                                             Pageable pageable);

    @Query("SELECT l FROM Fine l " +
            "WHERE " +
            "((:searchBy = 'loanId' AND l.loan.loan_id LIKE %:query%) OR " +
            "(:searchBy = 'fineId' AND l.fine_id LIKE %:query%) OR " +
            "(:searchBy = 'bookId' AND l.loan.book.book_id LIKE %:query%) OR " +
            "(:searchBy = 'title' AND l.loan.book.title LIKE %:query%) OR " +
            "(:searchBy = 'fine' AND l.amount LIKE %:query%)) " +
            "AND l.loan.member.member_id = :member_id " +
            "AND (:statusFilter IS NULL OR l.status = :statusFilter)")
    Page<Fine> searchFinesByMemberWithStatusAndNotAny(@Param("query") String query,
                                              @Param("statusFilter") FineStatus statusFilter,
                                              @Param("searchBy") String searchBy,
                                                      @Param("member_id") Long member_id,
                                              Pageable pageable);
    Page<Fine> findAllByLoan_Member(Member member, Pageable pageable);

}