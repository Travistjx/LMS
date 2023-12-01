package com.app.lms.service.serviceimpl;

import com.app.lms.entity.*;
import com.app.lms.repository.BookRepository;
import com.app.lms.repository.LoanRepository;
import com.app.lms.repository.MemberRepository;
import com.app.lms.service.BookService;
import com.app.lms.entity.IdType;
import com.app.lms.service.LoanService;
import com.app.lms.service.MemberService;
import com.app.lms.web.BookDto;
import com.app.lms.web.LoanDto;
import com.app.lms.web.MemberDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LoanServiceImpl implements LoanService {
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final MemberService memberService;
    private final BookService bookService;

    @Autowired
    public LoanServiceImpl(MemberRepository memberRepository, BookRepository bookRepository, LoanRepository loanRepository,
                           MemberService memberService, BookService bookService) {
        this.memberRepository = memberRepository;
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
        this.memberService = memberService;
        this.bookService = bookService;
    }


    @Override
    public void issueBooks(LoanDto loanDto){
        Loan loan = new Loan();
        Book selectedBook = bookRepository.findById(loanDto.getBook().getBook_id()).get();
        Member selectedMember = memberRepository.findById(loanDto.getMember().getMember_id()).get();
        selectedBook.setStatus(BookStatus.CHECKED_OUT);
        loan.setBook(selectedBook);
        loan.setMember(selectedMember);
        loan.setLoanDateTime(loanDto.getLoanDateTime());
        loan.setDueDateTime(loanDto.getDueDateTime());
        loan.setFineAmount(0.0);
        loan.setDaysOverdue((long)0);

        loanRepository.save(loan);
    }

    @Override
    public List<LoanDto> searchLoans(Long bookId, Long memberId) {
        List<Loan> loans = loanRepository.findAll();
        List<LoanDto> filteredLoans = loans.stream()
                .filter(Objects::nonNull)
                .filter(loan -> {
                    if (bookId != null && memberId != null) {
                        Book book = loan.getBook();
                        Member member = loan.getMember();
                        return book != null && member != null &&
                                book.getBook_id() != null && member.getMember_id() != null &&
                                book.getBook_id().equals(bookId) &&
                                member.getMember_id().equals(memberId) &&
                                !loan.getStatus().equals(LoanStatus.RETURNED);
                    }
                    return true;
                }).map(this::convertEntityToDto)
                .collect(Collectors.toList());

        return filteredLoans;
    }

    @Override
    public void returnBooks(Long id){
        Loan loan = loanRepository.findById(id).get();
        loan.setStatus(LoanStatus.RETURNED);
        loan.setReturnedDateTime(LocalDateTime.now());
        loan.getBook().setStatus(BookStatus.AVAILABLE);
        loanRepository.save(loan);
    }

    public LoanDto convertEntityToDto(Loan loan) {
        if (loan == null) return null;
        LoanDto loanDto = new LoanDto();
        loanDto.setLoan_id(loan.getLoan_id());
        loanDto.setLoanDateTime(loan.getLoanDateTime());
        loanDto.setDueDateTime(loan.getDueDateTime());
        loanDto.setReturnedDateTime(loan.getReturnedDateTime());
        loanDto.setFineAmount(loan.getFineAmount());
        loanDto.setDaysOverdue(loan.getDaysOverdue());
        loanDto.setStatus(loan.getStatus());

        // Convert book and member entities to DTOs
        BookDto bookDto = bookService.convertEntityToDto(loan.getBook());
        MemberDto memberDto = memberService.convertEntityToDto(loan.getMember());

        loanDto.setBook(bookDto);
        loanDto.setMember(memberDto);

        // Set the status here based on the loan's status
        if (loan.getStatus() == LoanStatus.ACTIVE) {
            loanDto.setStatus(LoanStatus.ACTIVE);
        } else if (loan.getStatus() == LoanStatus.RETURNED) {
            loanDto.setStatus(LoanStatus.RETURNED);
        } else if (loan.getStatus() == LoanStatus.OVERDUE) {
            loanDto.setStatus(LoanStatus.OVERDUE);
        }
        return loanDto;
    }

    @Override
    public Page<LoanDto> searchLoans(String query, Pageable pageable, Optional<Long> id, IdType idType,
                                     String statusFilter, String searchBy, String sort, String order) {

        Page<Loan> selectedLoans = null;

        LoanStatus status = LoanStatus.ACTIVE;
        if (statusFilter.equals("ALL")){
            status = null;
        }
        else if (statusFilter.equals("OVERDUE")){
            status = LoanStatus.OVERDUE;
        }
        else if (statusFilter.equals("ACTIVE")){
            status = LoanStatus.ACTIVE;
        }
        else if (statusFilter.equals("RETURNED")){
            status = LoanStatus.RETURNED;
        }

        Sort.Direction direction = Sort.Direction.ASC;

        if (order.equals("desc")){
            direction = Sort.Direction.DESC;
        }

        Sort sortable = Sort.by(direction, "loan_id"); // Default sorting by title



        if (sort != null) {
            switch (sort) {
                case "memberId":
                    sortable = Sort.by(direction, "member.member_id");
                    break;
                case "name":
                    sortable = Sort.by(direction, "member.firstName");
                    break;
                case "bookId":
                    sortable = Sort.by(direction, "book.book_id");
                    break;
                case "title":
                    sortable = Sort.by(direction, "book.title");
                    break;
                case "loanDateTime":
                    sortable = Sort.by(direction, "loanDateTime");
                    break;
                case "returnedDateTime":
                    sortable = Sort.by(direction, "returnedDateTime");
                    break;
                case "dueDateTime":
                    sortable = Sort.by(direction, "dueDateTime");
                    break;
                case "daysOverdue":
                    sortable = Sort.by(direction, "daysOverdue");
                    break;
                case "fine":
                    sortable = Sort.by(direction, "fineAmount");
                    break;
            }
        }

        if (id.isPresent()){
            Long selectedId = id.get();

            if (idType == IdType.MEMBER_ID) {
                if (searchBy.equals("any")){
                    selectedLoans = loanRepository.searchLoansByMemberWithStatusAndByAny(query, selectedId, status, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));
                }
                else {
                    selectedLoans = loanRepository.searchLoansByMemberWithStatusAndNotAny(query, selectedId, status, searchBy, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));
                }
            }
            if (idType == IdType.BOOK_ID){
                if (searchBy.equals("any")){
                    selectedLoans = loanRepository.searchLoansByBookWithStatusAndByAny(query, selectedId, status, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));
                }
                else {
                    selectedLoans = loanRepository.searchLoansByBookWithStatusAndNotAny(query, selectedId, status, searchBy, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));
                }
            }
        }
        else {
            if (searchBy.equals("any")){
                selectedLoans = loanRepository.searchLoansWithStatusAndByAny(query, status, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));
            }
            else {
                selectedLoans = loanRepository.searchLoansWithStatusAndNotAny(query, status, searchBy, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));
            }
        }


        List <LoanDto> selectedLoansDto = selectedLoans.getContent().stream().map(this::convertEntityToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(selectedLoansDto, pageable, selectedLoans.getTotalElements());
    }


    @Override
    public Page<LoanDto> findPaginated(int pageNo, int pageSize, Optional<Long> id, IdType idType) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<Loan> loanPage = this.loanRepository.findAll(pageable);

        if (id.isPresent()){
            Long selectedId = id.get();

            if (idType == IdType.MEMBER_ID){
                Member member = memberRepository.findById(selectedId).get();
                loanPage = this.loanRepository.findAllByMember(member, pageable);
            }

            if (idType == IdType.BOOK_ID){
                Book book = bookRepository.findById(selectedId).get();
                loanPage = this.loanRepository.findAllByBook(book, pageable);
            }
        }

        List <LoanDto> selectedLoansDto = loanPage.getContent().stream().map(this::convertEntityToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(selectedLoansDto, pageable, loanPage.getTotalElements());
    }

    @Override
    public void deleteLoans(Long id){
        Loan loanToDelete = loanRepository.findById(id).orElse(null);

        if (loanToDelete != null) {
            // Delete the loan
            loanRepository.delete(loanToDelete);
        }
    }

    @Override
    public Optional<LoanDto> findById(Long id){
        Optional<Loan>loan = loanRepository.findById(id);
        return loan.map(this::convertEntityToDto);
    }

    @Override
    public List<LoanDto> findAllLoans() {
        List<Loan> loans = loanRepository.findAll();
        return loans.stream().map((loan) -> convertEntityToDto(loan))
                .collect(Collectors.toList());
    }

    @Override
    public void updateLoans(LoanDto loan, Long loanId, Long memberId, Long bookId) {
        Optional<Loan> selectedLoan = loanRepository.findById(loanId);

        if (selectedLoan.isPresent()){
            Loan existingLoan = selectedLoan.get();
            Member member = memberRepository.findById(memberId).get();
            existingLoan.setMember(member);

            Book book = bookRepository.findById(bookId).get();
            existingLoan.setBook(book);
            existingLoan.setLoanDateTime(loan.getLoanDateTime());
            existingLoan.setDueDateTime(loan.getDueDateTime());
            existingLoan.setReturnedDateTime(loan.getReturnedDateTime());
            existingLoan.setStatus(loan.getStatus());

            loanRepository.save(existingLoan);
        }

    }

    @Override
    public Long findAllActiveLoansCount (){
        List<Loan> activeLoans = loanRepository.findAllByStatus(LoanStatus.ACTIVE);
        return (long) activeLoans.size();
    }

    @Override
    public Long findAllOverdueLoansCount (){
        List<Loan> activeLoans = loanRepository.findAllByStatus(LoanStatus.OVERDUE);
        return (long) activeLoans.size();
    }

    @Override
    public Long findAllActiveLoansByIdCount (MemberDto memberDto){
        Member member = memberRepository.findByEmail(memberDto.getEmail());
        List<Loan> activeLoans = loanRepository.findAllByStatusAndMember(LoanStatus.ACTIVE, member);
        return (long) activeLoans.size();
    }

    @Override
    public Long findAllOverdueLoansByIdCount(MemberDto memberDto) {
        Member member = memberRepository.findByEmail(memberDto.getEmail());
        List<Loan> overdueLoansById = loanRepository.findAllByStatusAndMember(LoanStatus.OVERDUE, member);
        return (long) overdueLoansById.size();
    }

    @Override
    public Long findLoansByMemberCount (MemberDto memberDto){
        List <Loan> loanList = loanRepository.findAll();
        List <LoanDto> matchedLoans = loanList.stream().filter(loan -> loan.getMember().getMember_id().equals(memberDto.getMember_id()))
                .map(this::convertEntityToDto).collect(Collectors.toList());

        return (long) matchedLoans.size();
    }
}
