package com.app.lms.service;

import com.app.lms.entity.IdType;
import com.app.lms.entity.Loan;
import com.app.lms.web.LoanDto;
import com.app.lms.web.MemberDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface LoanService {
    void issueBooks(LoanDto loanDto);

    List<LoanDto> searchLoans(Long bookId, Long memberId);

    public void returnBooks(Long id);

    Page<LoanDto> findPaginated(int pageNo, int pageSize, Optional<Long> id, IdType idType);

    Page<LoanDto> searchLoans(String query, Pageable pageable, Optional<Long> id, IdType idType, String statusFilter, String searchBy,
                              String sort, String order);

    void deleteLoans(Long id);

    Optional<LoanDto> findById(Long id);

    void updateLoans(LoanDto loan, Long loanId, Long memberId, Long bookId);

//    List<LoanDto> findByMemberId(Long id);
    Long findAllActiveLoansCount ();

    Long findAllOverdueLoansCount();

    Long findAllOverdueLoansByIdCount(MemberDto member);

    Long findAllActiveLoansByIdCount(MemberDto member);

    Long findLoansByMemberCount(MemberDto memberDto);

    LoanDto convertEntityToDto(Loan loan);

    List<LoanDto> findAllLoans();
}
