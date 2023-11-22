package com.app.lms.repository;

import com.app.lms.entity.Book;
import com.app.lms.entity.Loan;
import com.app.lms.entity.LoanStatus;
import com.app.lms.entity.Member;
import com.app.lms.web.MemberDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findAllByStatus(LoanStatus status);

    List <Loan> findAllByStatusAndMember(LoanStatus status, Member member);

    List<Loan> findOverdueLoansByDueDateTimeBefore(LocalDateTime today);

    @Query("SELECT l FROM Loan l " +
            "WHERE " +
            "((:searchBy = 'loanId' AND l.loan_id LIKE %:query%) OR " +
            "(:searchBy = 'loanDateTime' AND l.loanDateTime LIKE %:query%) OR " +
            "(:searchBy = 'dueDateTime' AND l.dueDateTime LIKE %:query%) OR " +
            "(:searchBy = 'returnedDateTime' AND l.returnedDateTime IS NOT NULL AND l.returnedDateTime LIKE %:query%) OR " +
            "((:searchBy = 'name' AND l.member.firstName LIKE %:query%) OR " +
            "(:searchBy = 'name' AND l.member.lastName LIKE %:query%)) OR " +
            "(:searchBy = 'memberId' AND l.member.member_id LIKE %:query%) OR " +
            "(:searchBy = 'bookId' AND l.book.book_id LIKE %:query%) OR " +
            "(:searchBy = 'title' AND l.book.title LIKE %:query%) OR " +
            "(:searchBy = 'daysOverdue' AND l.daysOverdue LIKE %:query%) OR " +
            "(:searchBy = 'fine' AND l.fineAmount LIKE %:query%)) " +
            "AND (:statusFilter IS NULL OR l.status = :statusFilter)")
    Page<Loan> searchLoansWithStatusAndNotAny(@Param("query") String query,
                                                  @Param("statusFilter") LoanStatus statusFilter,
                                                  @Param("searchBy") String searchBy,
                                                  Pageable pageable);

    @Query("SELECT l FROM Loan l " +
            "WHERE (l.loan_id LIKE %:query% OR " +
            "l.loanDateTime LIKE %:query% OR " +
            "l.dueDateTime LIKE %:query% OR " +
            "(l.returnedDateTime IS NOT NULL AND l.returnedDateTime LIKE %:query%) OR " +
            "l.member.firstName LIKE %:query% OR " +
            "l.member.lastName LIKE %:query% OR " +
            "l.member.member_id LIKE %:query% OR " +
            "l.status LIKE %:query% OR " +
            "l.book.book_id LIKE %:query% OR " +
            "l.fineAmount LIKE %:query% OR " +
            "l.book.title LIKE %:query%) " +
            "AND (:statusFilter IS NULL OR l.status = :statusFilter)")
    Page<Loan> searchLoansWithStatusAndByAny(@Param("query") String query,
                                          @Param("statusFilter") LoanStatus statusFilter,
                                            Pageable pageable);

    @Query("SELECT l FROM Loan l " +
            "WHERE (l.loan_id LIKE %:query% OR " +
            "l.loanDateTime LIKE %:query% OR " +
            "l.dueDateTime LIKE %:query% OR " +
            "(l.returnedDateTime IS NOT NULL AND l.returnedDateTime LIKE %:query%) OR " +
            "l.member.firstName LIKE %:query% OR " +
            "l.member.lastName LIKE %:query% OR " +
            "l.status LIKE %:query% OR " +
            "l.fineAmount LIKE %:query% OR " +
            "l.book.book_id LIKE %:query% OR " +
            "l.book.title LIKE %:query%) AND " +
            "l.member.member_id = :member_id AND " +
            "(:statusFilter IS NULL OR l.status = :statusFilter)")
    Page<Loan> searchLoansByMemberWithStatusAndByAny(@Param("query") String query,
                                   @Param("member_id") Long member_id,
                                   @Param("statusFilter") LoanStatus statusFilter,
                                   Pageable pageable);

    @Query("SELECT l FROM Loan l " +
            "WHERE " +
            "((:searchBy = 'loanId' AND l.loan_id LIKE %:query%) OR " +
            "(:searchBy = 'loanDateTime' AND l.loanDateTime LIKE %:query%) OR " +
            "(:searchBy = 'dueDateTime' AND l.dueDateTime LIKE %:query%) OR " +
            "(:searchBy = 'returnedDateTime' AND l.returnedDateTime IS NOT NULL AND l.returnedDateTime LIKE %:query%) OR " +
            "(:searchBy = 'bookId' AND l.book.book_id LIKE %:query%) OR " +
            "(:searchBy = 'title' AND l.book.title LIKE %:query%) OR " +
            "(:searchBy = 'daysOverdue' AND l.daysOverdue LIKE %:query%) OR " +
            "(:searchBy = 'fine' AND l.fineAmount LIKE %:query%)) AND " +
            "(l.member.member_id = :member_id) " +
            "AND (:statusFilter IS NULL OR l.status = :statusFilter)")
    Page<Loan> searchLoansByMemberWithStatusAndNotAny(@Param("query") String query,
                                                      @Param("member_id") Long member_id,
                                                    @Param("statusFilter") LoanStatus statusFilter,
                                                    @Param("searchBy") String searchBy,
                                                    Pageable pageable);

    @Query("SELECT l FROM Loan l " +
            "WHERE (l.loan_id LIKE %:query% OR " +
            "l.loanDateTime LIKE %:query% OR " +
            "l.dueDateTime LIKE %:query% OR " +
            "(l.returnedDateTime IS NOT NULL AND l.returnedDateTime LIKE %:query%) OR " +
            "l.member.firstName LIKE %:query% OR " +
            "l.member.lastName LIKE %:query% OR " +
            "l.member.member_id LIKE %:query% OR " +
            "l.status LIKE %:query% OR " +
            "l.book.title LIKE %:query%) AND " +
            "l.book.book_id = :book_id " +
            "AND (:statusFilter IS NULL OR l.status = :statusFilter)")
    Page<Loan> searchLoansByBookWithStatusAndByAny(@Param("query") String query,
                                 @Param("book_id") Long book_id,
                                 @Param("statusFilter") LoanStatus statusFilter,
                                 Pageable pageable);

    @Query("SELECT l FROM Loan l " +
            "WHERE " +
            "((:searchBy = 'loanId' AND l.loan_id LIKE %:query%) OR " +
            "(:searchBy = 'loanDateTime' AND l.loanDateTime LIKE %:query%) OR " +
            "(:searchBy = 'dueDateTime' AND l.dueDateTime LIKE %:query%) OR " +
            "(:searchBy = 'returnedDateTime' AND l.returnedDateTime IS NOT NULL AND l.returnedDateTime LIKE %:query%) OR " +
            "((:searchBy = 'name' AND l.member.firstName LIKE %:query%) OR " +
            "(:searchBy = 'name' AND l.member.lastName LIKE %:query%)) OR " +
            "(:searchBy = 'memberId' AND l.member.member_id LIKE %:query%) OR " +
            "(:searchBy = 'daysOverdue' AND l.daysOverdue LIKE %:query%) OR " +
            "(:searchBy = 'fine' AND l.fineAmount LIKE %:query%)) AND " +
            "(l.book.book_id = :book_id) " +
            "AND (:statusFilter IS NULL OR l.status = :statusFilter)")
    Page<Loan> searchLoansByBookWithStatusAndNotAny(@Param("query") String query,
                                                    @Param("book_id") Long book_id,
                                              @Param("statusFilter") LoanStatus statusFilter,
                                              @Param("searchBy") String searchBy,
                                              Pageable pageable);

    Page<Loan> findAllByMember(Member member, Pageable pageable);
    Page<Loan> findAllByBook(Book book, Pageable pageable);
}

