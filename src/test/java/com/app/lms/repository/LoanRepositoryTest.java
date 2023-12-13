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
class LoanRepositoryTest {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BookRepository bookRepository;

    @Test
    void itShouldFindAll() {
        Loan loan = new Loan();
        loan.setStatus(LoanStatus.RETURNED);
        loanRepository.save(loan);

        List<Loan> loans = loanRepository.findAll();

        boolean exists = false;
        if (loans != null) exists = true;
        assertThat(exists).isTrue();
    }

    @Test
    void itShouldFindPageOfLoans() {
        Loan loan = new Loan();
        loan.setStatus(LoanStatus.RETURNED);
        loanRepository.save(loan);

        int pageSize = 5;
        Pageable pageable = PageRequest.of(0, pageSize);

        Page<Loan> loans = loanRepository.findAll(pageable);

        boolean exists = false;
        if (loans != null) exists = true;
        assertThat(exists).isTrue();
    }
    @Test
    void itShouldFindAllLoansByStatus() {
        Loan loan = new Loan();
        loan.setStatus(LoanStatus.RETURNED);
        loanRepository.save(loan);

        List<Loan> loanList = loanRepository.findAllByStatus(LoanStatus.RETURNED);

        boolean exist = false;

        if (!loanList.isEmpty()) exist  = true;

        assertThat(exist).isTrue();
    }

    @Test
    void itShouldNotFindAllLoansByStatus() {
        List<Loan> loanList = loanRepository.findAllByStatus(LoanStatus.RETURNED);

        boolean exist = true;

        if (loanList.isEmpty()) exist  = false;

        assertThat(exist).isFalse();
    }

    @Test
    void itShouldFindAllLoansByStatusAndMember() {
        Loan loan = new Loan();
        loan.setStatus(LoanStatus.RETURNED);
        Member member = new Member();
        member.setEmail("georgeng@gmail.com");
        memberRepository.save(member);
        loan.setMember(member);
        loanRepository.save(loan);

        List<Loan> loanList = loanRepository.findAllByStatusAndMember(LoanStatus.RETURNED, member);

        boolean exist = false;

        if (!loanList.isEmpty()) exist  = true;

        assertThat(exist).isTrue();
    }

    @Test
    void itShouldNotFindAllLoansByStatusAndMember() {
        Member member = new Member();
        member.setEmail("georgeng@gmail.com");
        memberRepository.save(member);

        List<Loan> loanList = loanRepository.findAllByStatusAndMember(LoanStatus.RETURNED, member);

        boolean exist = true;

        if (loanList.isEmpty()) exist  = false;

        assertThat(exist).isFalse();
    }

    @Test
    void itShouldFindOverdueLoansByDueDateTimeBefore() {
        Loan loan = new Loan();
        loan.setStatus(LoanStatus.OVERDUE);
        LocalDateTime customDateTime = LocalDateTime.of(2023, 10, 30, 14, 30);
        loan.setDueDateTime(customDateTime);
        loanRepository.save(loan);

        List<Loan> loanList = loanRepository.findOverdueLoansByDueDateTimeBefore(LocalDateTime.of(2023, 11, 30, 14, 30));

        boolean exist = false;

        if (!loanList.isEmpty()) exist  = true;

        assertThat(exist).isTrue();
    }

    @Test
    void itShouldNotFindOverdueLoansByDueDateTimeBefore() {
        List<Loan> loanList = loanRepository.findOverdueLoansByDueDateTimeBefore(LocalDateTime.of(2023, 8, 30, 14, 30));

        boolean exist = true;

        if (loanList.isEmpty()) exist  = false;

        assertThat(exist).isFalse();
    }

    @Test
    void itShouldFindLoansBySearchLoansWithStatusAndNotAny() {
        Loan loan = new Loan();
        loan.setStatus(LoanStatus.OVERDUE);
        loan.setDaysOverdue((long)4);
        loan.setFineAmount(15.0);
        loan.setLoanDateTime(LocalDateTime.of(2023, 10, 30, 14, 30));
        loan.setDueDateTime(LocalDateTime.of(2023, 10, 30, 14, 30));
        loan.setReturnedDateTime(LocalDateTime.of(2023, 10, 30, 14, 30));

        Member member = new Member();
        member.setEmail("georgeng@gmail.com");
        memberRepository.save(member);

        Book book = new Book();
        book.setTitle("harry potter");
        bookRepository.save(book);

        loan.setBook(book);
        loan.setMember(member);

        loanRepository.save(loan);

        int pageSize = 5;

        Pageable pageable = PageRequest.of(0, pageSize);
        String query = "4";
        String searchBy = "daysOverdue";
        LoanStatus statusFilter = LoanStatus.OVERDUE;

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "loan_id");
        Page<Loan> selectedLoans = loanRepository.searchLoansWithStatusAndNotAny(query, statusFilter, searchBy,PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));

        boolean exists = false;
        if (selectedLoans.hasContent()) exists = true;

        assertThat(exists).isTrue();
    }

    @Test
    void itShouldNotFindLoansBySearchLoansWithStatusAndNotAny() {
        int pageSize = 5;

        Pageable pageable = PageRequest.of(0, pageSize);
        String query = "4";
        String searchBy = "daysOverdue";
        LoanStatus statusFilter = LoanStatus.OVERDUE;

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "loan_id");
        Page<Loan> selectedLoans = loanRepository.searchLoansWithStatusAndNotAny(query, statusFilter, searchBy,PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));

        boolean exists = true;
        if (!selectedLoans.hasContent()) exists = false;

        assertThat(exists).isFalse();
    }

    @Test
    void itShouldFindLoansBySearchLoansWithStatusAndByAny() {
        Loan loan = new Loan();
        loan.setStatus(LoanStatus.OVERDUE);
        loan.setDaysOverdue((long)4);
        loan.setFineAmount(15.0);
        loan.setLoanDateTime(LocalDateTime.of(2023, 10, 30, 14, 30));
        loan.setDueDateTime(LocalDateTime.of(2023, 10, 30, 14, 30));
        loan.setReturnedDateTime(LocalDateTime.of(2023, 10, 30, 14, 30));

        Member member = new Member();
        member.setEmail("georgeng@gmail.com");
        memberRepository.save(member);

        Book book = new Book();
        book.setTitle("harry potter");
        bookRepository.save(book);

        loan.setBook(book);
        loan.setMember(member);

        loanRepository.save(loan);

        int pageSize = 5;

        Pageable pageable = PageRequest.of(0, pageSize);
        String query = "4";
        LoanStatus statusFilter = LoanStatus.OVERDUE;

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "loan_id");
        Page<Loan> selectedLoans = loanRepository.searchLoansWithStatusAndByAny(query, statusFilter,PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));

        boolean exists = false;
        if (selectedLoans.hasContent()) exists = true;

        assertThat(exists).isTrue();
    }

    @Test
    void itShouldNotFindLoansBySearchLoansWithStatusAndByAny() {
        int pageSize = 5;

        Pageable pageable = PageRequest.of(0, pageSize);
        String query = "4";
        LoanStatus statusFilter = LoanStatus.OVERDUE;

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "loan_id");
        Page<Loan> selectedLoans = loanRepository.searchLoansWithStatusAndByAny(query, statusFilter,PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));

        boolean exists = true;
        if (!selectedLoans.hasContent()) exists = false;

        assertThat(exists).isFalse();
    }

    @Test
    void itShouldFindLoansBySearchLoansByMemberWithStatusAndByAny() {
        Loan loan = new Loan();
        loan.setStatus(LoanStatus.OVERDUE);
        loan.setDaysOverdue((long)4);
        loan.setFineAmount(15.0);
        loan.setLoanDateTime(LocalDateTime.of(2023, 10, 30, 14, 30));
        loan.setDueDateTime(LocalDateTime.of(2023, 10, 30, 14, 30));
        loan.setReturnedDateTime(LocalDateTime.of(2023, 10, 30, 14, 30));

        Member member = new Member();
        member.setEmail("georgeng@gmail.com");
        memberRepository.save(member);

        Book book = new Book();
        book.setTitle("harry potter");
        bookRepository.save(book);

        loan.setBook(book);
        loan.setMember(member);

        loanRepository.save(loan);

        int pageSize = 5;

        Pageable pageable = PageRequest.of(0, pageSize);
        String query = "4";
        LoanStatus statusFilter = LoanStatus.OVERDUE;

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "loan_id");
        Page<Loan> selectedLoans = loanRepository.searchLoansByMemberWithStatusAndByAny(query, member.getMember_id(), statusFilter, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));

        boolean exists = false;
        if (selectedLoans.hasContent()) exists = true;

        assertThat(exists).isTrue();
    }

    @Test
    void itShouldNotFindLoansBySearchLoansByMemberWithStatusAndByAny() {
        Member member = new Member();
        member.setEmail("georgeng@gmail.com");
        memberRepository.save(member);

        int pageSize = 5;

        Pageable pageable = PageRequest.of(0, pageSize);
        String query = "4";
        LoanStatus statusFilter = LoanStatus.OVERDUE;

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "loan_id");
        Page<Loan> selectedLoans = loanRepository.searchLoansByMemberWithStatusAndByAny(query, member.getMember_id(), statusFilter, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));

        boolean exists = true;
        if (!selectedLoans.hasContent()) exists = false;

        assertThat(exists).isFalse();
    }

    @Test
    void itShouldFindLoansBySearchLoansByMemberWithStatusAndNotAny() {
        Loan loan = new Loan();
        loan.setStatus(LoanStatus.OVERDUE);
        loan.setDaysOverdue((long)4);
        loan.setFineAmount(15.0);
        loan.setLoanDateTime(LocalDateTime.of(2023, 10, 30, 14, 30));
        loan.setDueDateTime(LocalDateTime.of(2023, 10, 30, 14, 30));
        loan.setReturnedDateTime(LocalDateTime.of(2023, 10, 30, 14, 30));

        Member member = new Member();
        member.setEmail("georgeng@gmail.com");
        memberRepository.save(member);

        Book book = new Book();
        book.setTitle("harry potter");
        bookRepository.save(book);

        loan.setBook(book);
        loan.setMember(member);

        loanRepository.save(loan);

        int pageSize = 5;

        Pageable pageable = PageRequest.of(0, pageSize);
        String query = "4";
        String searchBy = "daysOverdue";
        LoanStatus statusFilter = LoanStatus.OVERDUE;

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "loan_id");
        Page<Loan> selectedLoans = loanRepository.searchLoansByMemberWithStatusAndNotAny(query, member.getMember_id(), statusFilter, searchBy, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));

        boolean exists = false;
        if (selectedLoans.hasContent()) exists = true;

        assertThat(exists).isTrue();
    }

    @Test
    void itShouldNotFindLoansBySearchLoansByMemberWithStatusAndNotAny() {
        Member member = new Member();
        member.setEmail("georgeng@gmail.com");
        memberRepository.save(member);

        int pageSize = 5;

        Pageable pageable = PageRequest.of(0, pageSize);
        String query = "4";
        String searchBy = "daysOverdue";
        LoanStatus statusFilter = LoanStatus.OVERDUE;

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "loan_id");
        Page<Loan> selectedLoans = loanRepository.searchLoansByMemberWithStatusAndNotAny(query, member.getMember_id(), statusFilter, searchBy, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));

        boolean exists = true;
        if (!selectedLoans.hasContent()) exists = false;

        assertThat(exists).isFalse();
    }

    @Test
    void itShouldFindLoansBySearchLoansByBookWithStatusAndByAny() {
        Loan loan = new Loan();
        loan.setStatus(LoanStatus.OVERDUE);
        loan.setDaysOverdue((long)4);
        loan.setFineAmount(15.0);
        loan.setLoanDateTime(LocalDateTime.of(2023, 10, 30, 14, 30));
        loan.setDueDateTime(LocalDateTime.of(2023, 10, 30, 14, 30));
        loan.setReturnedDateTime(LocalDateTime.of(2023, 10, 30, 14, 30));

        Member member = new Member();
        member.setEmail("georgeng@gmail.com");
        memberRepository.save(member);

        Book book = new Book();
        book.setTitle("harry potter");
        bookRepository.save(book);

        loan.setBook(book);
        loan.setMember(member);

        loanRepository.save(loan);

        int pageSize = 5;

        Pageable pageable = PageRequest.of(0, pageSize);
        String query = "4";
        LoanStatus statusFilter = LoanStatus.OVERDUE;

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "loan_id");
        Page<Loan> selectedLoans = loanRepository.searchLoansByBookWithStatusAndByAny(query, book.getBook_id(), statusFilter, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));

        boolean exists = false;
        if (selectedLoans.hasContent()) exists = true;

        assertThat(exists).isTrue();
    }

    @Test
    void itShouldNotFindLoansBySearchLoansByBookWithStatusAndByAny() {
        Book book = new Book();
        book.setTitle("harry potter");
        bookRepository.save(book);

        int pageSize = 5;

        Pageable pageable = PageRequest.of(0, pageSize);
        String query = "4";
        LoanStatus statusFilter = LoanStatus.OVERDUE;

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "loan_id");
        Page<Loan> selectedLoans = loanRepository.searchLoansByBookWithStatusAndByAny(query, book.getBook_id(), statusFilter, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));

        boolean exists = true;
        if (!selectedLoans.hasContent()) exists = false;

        assertThat(exists).isFalse();
    }

    @Test
    void itShouldFindLoansBySearchLoansByBookWithStatusAndNotAny() {
        Loan loan = new Loan();
        loan.setStatus(LoanStatus.OVERDUE);
        loan.setDaysOverdue((long)4);
        loan.setFineAmount(15.0);
        loan.setLoanDateTime(LocalDateTime.of(2023, 10, 30, 14, 30));
        loan.setDueDateTime(LocalDateTime.of(2023, 10, 30, 14, 30));
        loan.setReturnedDateTime(LocalDateTime.of(2023, 10, 30, 14, 30));

        Member member = new Member();
        member.setEmail("georgeng@gmail.com");
        memberRepository.save(member);

        Book book = new Book();
        book.setTitle("harry potter");
        bookRepository.save(book);

        loan.setBook(book);
        loan.setMember(member);

        loanRepository.save(loan);

        int pageSize = 5;

        Pageable pageable = PageRequest.of(0, pageSize);
        String query = "4";
        String searchBy = "daysOverdue";
        LoanStatus statusFilter = LoanStatus.OVERDUE;

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "loan_id");
        Page<Loan> selectedLoans = loanRepository.searchLoansByBookWithStatusAndNotAny(query, book.getBook_id(), statusFilter, searchBy, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));

        boolean exists = false;
        if (selectedLoans.hasContent()) exists = true;

        assertThat(exists).isTrue();
    }

    @Test
    void itShouldNotFindLoansBySearchLoansByBookWithStatusAndNotAny() {
        Book book = new Book();
        book.setTitle("harry potter");
        bookRepository.save(book);

        int pageSize = 5;

        Pageable pageable = PageRequest.of(0, pageSize);
        String query = "4";
        String searchBy = "daysOverdue";
        LoanStatus statusFilter = LoanStatus.OVERDUE;

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "loan_id");
        Page<Loan> selectedLoans = loanRepository.searchLoansByBookWithStatusAndNotAny(query, book.getBook_id(), statusFilter, searchBy, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));

        boolean exists = true;
        if (!selectedLoans.hasContent()) exists = false;

        assertThat(exists).isFalse();
    }

    @Test
    void itShouldFindLoansBySearchAllByMember() {
        Loan loan = new Loan();
        loan.setStatus(LoanStatus.OVERDUE);
        loan.setDaysOverdue((long)4);
        loan.setFineAmount(15.0);
        loan.setLoanDateTime(LocalDateTime.of(2023, 10, 30, 14, 30));
        loan.setDueDateTime(LocalDateTime.of(2023, 10, 30, 14, 30));
        loan.setReturnedDateTime(LocalDateTime.of(2023, 10, 30, 14, 30));

        Member member = new Member();
        member.setEmail("georgeng@gmail.com");
        memberRepository.save(member);

        Book book = new Book();
        book.setTitle("harry potter");
        bookRepository.save(book);

        loan.setBook(book);
        loan.setMember(member);


        loanRepository.save(loan);


        int pageSize = 5;

        Pageable pageable = PageRequest.of(0, pageSize);
        Page<Loan> selectedLoans = loanRepository.findAllByMember(member,  pageable);

        boolean exist = false;

        if (!selectedLoans.isEmpty()) exist  = true;

        assertThat(exist).isTrue();
    }

    @Test
    void itShouldNotFindLoansBySearchAllByMember() {
        Member member = new Member();
        member.setEmail("georgeng@gmail.com");
        memberRepository.save(member);

        int pageSize = 5;

        Pageable pageable = PageRequest.of(0, pageSize);
        Page<Loan> selectedLoans = loanRepository.findAllByMember(member,  pageable);

        boolean exist = true;

        if (selectedLoans.isEmpty()) exist  = false;

        assertThat(exist).isFalse();
    }

    @Test
    void itShouldFindLoansByFindAllByBook() {
        Loan loan = new Loan();
        loan.setStatus(LoanStatus.OVERDUE);
        loan.setDaysOverdue((long)4);
        loan.setFineAmount(15.0);
        loan.setLoanDateTime(LocalDateTime.of(2023, 10, 30, 14, 30));
        loan.setDueDateTime(LocalDateTime.of(2023, 10, 30, 14, 30));
        loan.setReturnedDateTime(LocalDateTime.of(2023, 10, 30, 14, 30));

        Member member = new Member();
        member.setEmail("georgeng@gmail.com");
        memberRepository.save(member);

        Book book = new Book();
        book.setTitle("harry potter");
        bookRepository.save(book);

        loan.setBook(book);
        loan.setMember(member);


        loanRepository.save(loan);


        int pageSize = 5;

        Pageable pageable = PageRequest.of(0, pageSize);
        Page<Loan> selectedLoans = loanRepository.findAllByBook(book,  pageable);

        boolean exist = false;

        if (!selectedLoans.isEmpty()) exist  = true;

        assertThat(exist).isTrue();
    }

    @Test
    void itShouldNotFindLoansByFindAllByBook() {
        Book book = new Book();
        book.setTitle("harry potter");
        bookRepository.save(book);

        int pageSize = 5;

        Pageable pageable = PageRequest.of(0, pageSize);
        Page<Loan> selectedLoans = loanRepository.findAllByBook(book,  pageable);

        boolean exist = true;

        if (selectedLoans.isEmpty()) exist  = false;

        assertThat(exist).isFalse();
    }
}