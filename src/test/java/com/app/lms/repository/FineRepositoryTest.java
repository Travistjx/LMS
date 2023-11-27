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
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class FineRepositoryTest {

    @Autowired
    private FineRepository fineRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BookRepository bookRepository;

    @Test
    void itShouldFindByLoan() {
        Loan loan = new Loan();
        loan.setStatus(LoanStatus.RETURNED);
        loanRepository.save(loan);

        Fine fine = new Fine();
        fine.setLoan(loan);
        fineRepository.save(fine);

        // Check if a fine already exists for this loan
        List<Fine> selectedFine = fineRepository.findByLoan(loan);

        boolean exists = false;
        if (selectedFine != null) exists = true;
        assertThat(exists).isTrue();
    }

    @Test
    void itShouldNotFindByLoan() {
        Loan loan = new Loan();
        loan.setStatus(LoanStatus.RETURNED);
        loanRepository.save(loan);

        // Check if a fine already exists for this loan
        List<Fine> selectedFine = fineRepository.findByLoan(loan);

        boolean exists = true;
        if (selectedFine != null) exists = false;
        assertThat(exists).isFalse();
    }

    @Test
    void itShouldSearchFinesWithStatusAndByAny() {
        Loan loan = new Loan();
        loan.setStatus(LoanStatus.RETURNED);
        loan.setDaysOverdue((long)4);
        loan.setFineAmount(4.0);
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

        Fine fine = new Fine();
        fine.setStatus(FineStatus.PAID);
        fine.setAmount(4.0);
        fine.setDateTimeOfFine(LocalDateTime.now());
        fine.setLoan(loan);
        fineRepository.save(fine);

        int pageSize = 5;

        Pageable pageable = PageRequest.of(0, pageSize);
        String query = "4";
        FineStatus status = FineStatus.PAID;

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "fine_id");
        Page <Fine> fineList = fineRepository.searchFinesWithStatusAndByAny(query, status, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));

        boolean exists = false;
        if (fineList.hasContent()) exists = true;
        assertThat(exists).isTrue();
    }

    @Test
    void itShouldNotSearchFinesWithStatusAndByAny() {
        Loan loan = new Loan();
        loan.setStatus(LoanStatus.RETURNED);
        loan.setDaysOverdue((long)4);
        loan.setFineAmount(4.0);
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

        Fine fine = new Fine();
        fine.setStatus(FineStatus.PAID);
        fine.setAmount(4.0);
        fine.setDateTimeOfFine(LocalDateTime.now());
        fine.setLoan(loan);
        fineRepository.save(fine);

        int pageSize = 5;

        Pageable pageable = PageRequest.of(0, pageSize);
        String query = "3";
        FineStatus status = FineStatus.PAID;

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "fine_id");
        Page <Fine> fineList = fineRepository.searchFinesWithStatusAndByAny(query, status, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));

        boolean exists = true;
        if (!fineList.hasContent()) exists = false;
        assertThat(exists).isFalse();
    }

    @Test
    void itShouldSearchFinesWithStatusAndNotAny() {
        Loan loan = new Loan();
        loan.setStatus(LoanStatus.RETURNED);
        loan.setDaysOverdue((long)4);
        loan.setFineAmount(4.0);
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

        Fine fine = new Fine();
        fine.setStatus(FineStatus.PAID);
        fine.setAmount(4.0);
        fine.setDateTimeOfFine(LocalDateTime.now());
        fine.setLoan(loan);
        fineRepository.save(fine);

        int pageSize = 5;

        Pageable pageable = PageRequest.of(0, pageSize);
        String query = "4";
        String searchBy = "fine";
        FineStatus status = FineStatus.PAID;

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "fine_id");
        Page <Fine> fineList = fineRepository.searchFinesWithStatusAndNotAny(query, status, searchBy, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));

        boolean exists = false;
        if (fineList.hasContent()) exists = true;
        assertThat(exists).isTrue();
    }

    @Test
    void itShouldNotSearchFinesWithStatusAndNotAny() {
        Loan loan = new Loan();
        loan.setStatus(LoanStatus.RETURNED);
        loan.setDaysOverdue((long)4);
        loan.setFineAmount(4.0);
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

        Fine fine = new Fine();
        fine.setStatus(FineStatus.PAID);
        fine.setAmount(4.0);
        fine.setDateTimeOfFine(LocalDateTime.now());
        fine.setLoan(loan);
        fineRepository.save(fine);

        int pageSize = 5;

        Pageable pageable = PageRequest.of(0, pageSize);
        String query = "3";
        String searchBy = "fine";
        FineStatus status = FineStatus.PAID;

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "fine_id");
        Page <Fine> fineList = fineRepository.searchFinesWithStatusAndNotAny(query, status, searchBy, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));

        boolean exists = true;
        if (!fineList.hasContent()) exists = false;
        assertThat(exists).isFalse();
    }

    @Test
    void itShouldSearchFinesByMemberWithStatusAndByAny() {
        Loan loan = new Loan();
        loan.setStatus(LoanStatus.RETURNED);
        loan.setDaysOverdue((long)4);
        loan.setFineAmount(4.0);
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

        Fine fine = new Fine();
        fine.setStatus(FineStatus.PAID);
        fine.setAmount(4.0);
        fine.setDateTimeOfFine(LocalDateTime.now());
        fine.setLoan(loan);
        fineRepository.save(fine);

        int pageSize = 5;

        Pageable pageable = PageRequest.of(0, pageSize);
        String query = "4";
        FineStatus status = FineStatus.PAID;

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "fine_id");
        Page <Fine> fineList = fineRepository.searchFinesByMemberWithStatusAndByAny(query, status, member.getMember_id(), PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));

        boolean exists = false;
        if (fineList.hasContent()) exists = true;
        assertThat(exists).isTrue();
    }

    @Test
    void itShouldNotSearchFinesByMemberWithStatusAndByAny() {
        Loan loan = new Loan();
        loan.setStatus(LoanStatus.RETURNED);
        loan.setDaysOverdue((long)4);
        loan.setFineAmount(4.0);
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

        Fine fine = new Fine();
        fine.setStatus(FineStatus.PAID);
        fine.setAmount(4.0);
        fine.setDateTimeOfFine(LocalDateTime.now());
        fine.setLoan(loan);
        fineRepository.save(fine);

        int pageSize = 5;

        Pageable pageable = PageRequest.of(0, pageSize);
        String query = "3";
        FineStatus status = FineStatus.PAID;

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "fine_id");
        Page <Fine> fineList = fineRepository.searchFinesByMemberWithStatusAndByAny(query, status, member.getMember_id(), PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));

        boolean exists = true;
        if (!fineList.hasContent()) exists = false;
        assertThat(exists).isFalse();
    }

    @Test
    void itShouldSearchFinesByMemberWithStatusAndNotAny() {
        Loan loan = new Loan();
        loan.setStatus(LoanStatus.RETURNED);
        loan.setDaysOverdue((long)4);
        loan.setFineAmount(4.0);
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

        Fine fine = new Fine();
        fine.setStatus(FineStatus.PAID);
        fine.setAmount(4.0);
        fine.setDateTimeOfFine(LocalDateTime.now());
        fine.setLoan(loan);
        fineRepository.save(fine);

        int pageSize = 5;

        Pageable pageable = PageRequest.of(0, pageSize);
        String query = "4";
        String searchBy = "fine";
        FineStatus status = FineStatus.PAID;

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "fine_id");
        Page <Fine> fineList = fineRepository.searchFinesByMemberWithStatusAndNotAny(query, status, searchBy,
                member.getMember_id(), PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));

        boolean exists = false;
        if (fineList.hasContent()) exists = true;
        assertThat(exists).isTrue();
    }

    @Test
    void itShouldNotSearchFinesByMemberWithStatusAndNotAny() {
        Loan loan = new Loan();
        loan.setStatus(LoanStatus.RETURNED);
        loan.setDaysOverdue((long)4);
        loan.setFineAmount(4.0);
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

        Fine fine = new Fine();
        fine.setStatus(FineStatus.PAID);
        fine.setAmount(4.0);
        fine.setDateTimeOfFine(LocalDateTime.now());
        fine.setLoan(loan);
        fineRepository.save(fine);

        int pageSize = 5;

        Pageable pageable = PageRequest.of(0, pageSize);
        String query = "3";
        String searchBy = "fine";
        FineStatus status = FineStatus.PAID;

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "fine_id");
        Page <Fine> fineList = fineRepository.searchFinesByMemberWithStatusAndNotAny(query, status, searchBy,
                member.getMember_id(), PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));

        boolean exists = true;
        if (!fineList.hasContent()) exists = false;
        assertThat(exists).isFalse();
    }

    //Member is an attribute of Loan, and Loan is attribute of Fine
    @Test
    void itShouldFindAllByLoan_Member() {
        Loan loan = new Loan();
        loan.setStatus(LoanStatus.RETURNED);
        loan.setDaysOverdue((long)4);
        loan.setFineAmount(4.0);
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

        Fine fine = new Fine();
        fine.setStatus(FineStatus.PAID);
        fine.setAmount(4.0);
        fine.setDateTimeOfFine(LocalDateTime.now());
        fine.setLoan(loan);
        fineRepository.save(fine);

        int pageSize = 5;
        Pageable pageable = PageRequest.of(0, pageSize);
        Page <Fine> fineList = fineRepository.findAllByLoan_Member(
                member, pageable);

        boolean exists = false;
        if (fineList.hasContent()) exists = true;
        assertThat(exists).isTrue();
    }

    @Test
    void itShouldNotFindAllByLoan_Member() {
        Loan loan = new Loan();
        loan.setStatus(LoanStatus.RETURNED);
        loan.setDaysOverdue((long)4);
        loan.setFineAmount(4.0);
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
        Page <Fine> fineList = fineRepository.findAllByLoan_Member(
                member, pageable);

        boolean exists = true;
        if (!fineList.hasContent()) exists = false;
        assertThat(exists).isFalse();
    }
}