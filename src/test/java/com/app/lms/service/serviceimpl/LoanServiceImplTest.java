package com.app.lms.service.serviceimpl;

import com.app.lms.entity.*;
import com.app.lms.repository.AuthorRepository;
import com.app.lms.repository.BookRepository;
import com.app.lms.repository.LoanRepository;
import com.app.lms.repository.MemberRepository;
import com.app.lms.service.BookService;
import com.app.lms.service.LoanService;
import com.app.lms.service.MemberService;
import com.app.lms.web.BookDto;
import com.app.lms.web.LoanDto;
import com.app.lms.web.MemberDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceImplTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private MemberRepository memberRepository;

    private LoanService loanService;

    @Mock
    private MemberService memberService;

    @Mock
    private BookService bookService;

    @BeforeEach
    void setup (){
        loanService = new LoanServiceImpl(memberRepository, bookRepository, loanRepository,
                memberService, bookService);
    }


    @Test
    void canIssueBooks() {
        LoanDto loanDto = new LoanDto();
        loanDto.setDueDateTime(LocalDateTime.now());
        MemberDto memberDto = new MemberDto();
        BookDto bookDto = new BookDto();
        loanDto.setBook(bookDto);
        loanDto.setMember(memberDto);

        Book book = new Book();
        Member member = new Member();
        when(bookRepository.findById(bookDto.getBook_id())).thenReturn(Optional.of(book));
        when(memberRepository.findById(memberDto.getMember_id())).thenReturn(Optional.of(member));

        loanService.issueBooks(loanDto);

        ArgumentCaptor<Loan> loanArgumentCaptor = ArgumentCaptor.forClass(Loan.class);
        verify(loanRepository).save(loanArgumentCaptor.capture());
        Loan capturedLoan = loanArgumentCaptor.getValue();

        assertThat(capturedLoan.getDueDateTime()).isEqualTo(loanDto.getDueDateTime());
    }

    @Test
    void canFindListOfSearchLoans() {

        // Mock data
        Long bookId = 1L;
        Long memberId = 1L;

        Loan loan1 = new Loan();
        Book book1 = new Book();
        book1.setBook_id(1L);
        Member member1 = new Member();
        member1.setMember_id(1L);

        loan1.setBook(book1);
        loan1.setMember(member1);
        loan1.setStatus(LoanStatus.ACTIVE);

        List<Loan> mockLoans = List.of(loan1);

        when(loanRepository.findAll()).thenReturn(mockLoans);

        List<LoanDto> result = loanService.searchLoans(bookId, memberId);

        assertEquals(1L, result.size());
    }

    @Test
    void canReturnBooks() {
        Loan loan = new Loan();
        Book book = new Book();
        loan.setBook(book);
        when(loanRepository.findById(loan.getLoan_id())).thenReturn(Optional.of(loan));

        loanService.returnBooks(loan.getLoan_id());
        ArgumentCaptor <Loan> loanArgumentCaptor = ArgumentCaptor.forClass(Loan.class);
        verify(loanRepository).save(loanArgumentCaptor.capture());
        Loan capturedLoan = loanArgumentCaptor.getValue();

        assertThat(capturedLoan).isEqualTo(loan);
    }

    @Test
    void canConvertEntityToDto() {
        Loan loan = new Loan();
        loan.setStatus(LoanStatus.RETURNED);
        loan.setMember(new Member());
        loan.setFineAmount(0.0);
        loan.setReturnedDateTime(LocalDateTime.now());
        loan.setBook(new Book());
        loan.setDueDateTime(LocalDateTime.now());
        loan.setReturnedDateTime(LocalDateTime.now());
        loan.setDaysOverdue(1L);

        when(bookService.convertEntityToDto(any(Book.class))).thenReturn(new BookDto());
        when(memberService.convertEntityToDto(any(Member.class))).thenReturn(new MemberDto());

        LoanDto loanDto = loanService.convertEntityToDto(loan);

        assertThat(loanDto.getLoan_id()).isEqualTo(loan.getLoan_id());
        assertThat(loanDto.getStatus()).isEqualTo(loan.getStatus());
        assertThat(loanDto.getDaysOverdue()).isEqualTo(loan.getDaysOverdue());
        assertThat(loanDto.getReturnedDateTime()).isEqualTo(loan.getReturnedDateTime());
        assertThat(loanDto.getDueDateTime()).isEqualTo(loan.getDueDateTime());
        assertThat(loanDto.getLoanDateTime()).isEqualTo(loan.getLoanDateTime());
        assertThat(loanDto.getFineAmount()).isEqualTo(loan.getFineAmount());
        assertThat(loanDto.getBook().getBook_id()).isEqualTo(loan.getBook().getBook_id());
        assertThat(loanDto.getMember().getMember_id()).isEqualTo(loan.getMember().getMember_id());
    }

    @Test
    void canFindPageOfSearchLoans() {
        int pageSize = 5;
        int pageNo = 1;
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

        String query = "1";
        String searchBy = "fine";
        String status = "RETURNED";
        LoanStatus statusFilter = LoanStatus.RETURNED;
        String sort = "loan_id";
        String order = "asc";

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "loan_id");

        when(loanRepository.searchLoansWithStatusAndNotAny(query, statusFilter,searchBy,PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));


        loanService.searchLoans(query, pageable, Optional.empty(), IdType.NONE, status, searchBy, sort ,order);
        verify(loanRepository).searchLoansWithStatusAndNotAny(query, statusFilter,searchBy,PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));
    }

    @Test
    void canFindPaginated() {
        int pageSize = 5;
        int pageNo = 1;
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

        when(loanRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        loanService.findPaginated(pageNo, pageSize, Optional.empty(), IdType.NONE);


        verify(loanRepository).findAll(pageable);
    }

    @Test
    void canDeleteLoans() {
        Loan loan = new Loan();
        when(loanRepository.findById((long)1))
                .thenReturn(Optional.of(loan));

        loanService.deleteLoans((long)1);

        ArgumentCaptor <Loan> loanArgumentCaptor = ArgumentCaptor.forClass(Loan.class);
        verify(loanRepository).save(loanArgumentCaptor.capture());
        Loan capturedLoan = loanArgumentCaptor.getValue();

        assertThat(capturedLoan .isDeleted()).isEqualTo(true);
    }

    @Test
    void canFindById() {
        loanService.findById((long)1);
        verify(loanRepository).findById((long)1);
    }

    @Test
    void findAllLoans() {
        loanService.findAllLoans();
        verify(loanRepository).findAll();
    }

    @Test
    void updateLoans() {
        Loan loan = new Loan();
        Member member = new Member();
        member.setFirstName("David");
        Book book = new Book();
        book.setTitle("Harry");
        member.setMember_id(1L);
        book.setBook_id(1L);

        loan.setDaysOverdue(1L);
        loan.setLoan_id(1L);
        loan.setStatus(LoanStatus.RETURNED);
        loan.setMember(member);
        loan.setBook(book);

        LoanDto loanDto = new LoanDto();
        loanDto.setDaysOverdue(1L);
        loanDto.setStatus(LoanStatus.ACTIVE);


        when(loanRepository.findById(1L))
                .thenReturn(Optional.of(loan));

        when(memberRepository.findById(1L))
                .thenReturn(Optional.of(member));

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));

        loanService.updateLoans(loanDto, 1L, 1L, 1L);

        ArgumentCaptor <Loan> loanArgumentCaptor = ArgumentCaptor.forClass(Loan.class);
        verify(loanRepository).save(loanArgumentCaptor.capture());
        Loan capturedLoan = loanArgumentCaptor.getValue();

        assertThat(capturedLoan.getStatus()).isEqualTo(loanDto.getStatus());
        assertThat(capturedLoan.getDaysOverdue()).isEqualTo(loanDto.getDaysOverdue());
    }

    @Test
    void findAllActiveLoansCount() {
        loanService.findAllActiveLoansCount();
        verify(loanRepository).findAllByStatus(LoanStatus.ACTIVE);
    }

    @Test
    void findAllOverdueLoansCount() {
        loanService.findAllOverdueLoansCount();
        verify(loanRepository).findAllByStatus(LoanStatus.OVERDUE);
    }

    @Test
    void findAllActiveLoansByIdCount() {
        Member member = new Member();
        member.setEmail("hi@gmail.com");

        MemberDto memberDto = new MemberDto();
        memberDto.setEmail("hi@gmail.com");

        when(memberRepository.findByEmail("hi@gmail.com")).thenReturn(member);
        loanService.findAllActiveLoansByIdCount(memberDto);

        verify(loanRepository).findAllByStatusAndMember(LoanStatus.ACTIVE, member);
    }

    @Test
    void findAllOverdueLoansByIdCount() {
        Member member = new Member();
        member.setEmail("hi@gmail.com");

        MemberDto memberDto = new MemberDto();
        memberDto.setEmail("hi@gmail.com");

        when(memberRepository.findByEmail("hi@gmail.com")).thenReturn(member);
        loanService.findAllOverdueLoansByIdCount(memberDto);

        verify(loanRepository).findAllByStatusAndMember(LoanStatus.OVERDUE, member);
    }

    @Test
    void findLoansByMemberCount() {
        Member member = new Member();
        member.setMember_id(1L);

        Loan loan = new Loan();
        loan.setMember(member);

        MemberDto memberDto = new MemberDto();
        memberDto.setMember_id(1L);

        when(loanRepository.findAll()).thenReturn(List.of(loan));
        Long result = loanService.findLoansByMemberCount(memberDto);

        assertEquals(1L, result);
    }
}