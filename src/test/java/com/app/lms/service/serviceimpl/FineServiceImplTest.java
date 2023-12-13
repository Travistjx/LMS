package com.app.lms.service.serviceimpl;

import com.app.lms.entity.*;
import com.app.lms.repository.FineRepository;
import com.app.lms.repository.LoanRepository;
import com.app.lms.repository.MemberRepository;
import com.app.lms.service.FineService;
import com.app.lms.service.LoanService;
import com.app.lms.service.MemberService;
import com.app.lms.web.FineDto;
import com.app.lms.web.LoanDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FineServiceImplTest {

    @Mock
    private LoanRepository loanRepository;
    @Mock
    private FineRepository fineRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberService memberService;

    @Mock
    private LoanService loanService;

    private FineService fineService;

    @BeforeEach
    void setup (){
        fineService = new FineServiceImpl(loanRepository, fineRepository, memberRepository,
                memberService, loanService);
    }

    @Test
    void canCalculateFinesForOverdueLoans() {
        Loan loan = new Loan();
        loan.setDueDateTime(LocalDateTime.of(2023, 10, 30, 14, 30));
        Fine fine = new Fine();
        fine.setLoan(loan);

        when(loanRepository
                .findOverdueLoansByDueDateTimeBefore(any()))
                .thenReturn(List.of(loan));

        when(fineRepository.findByLoan(loan)).thenReturn(List.of(fine));

        fineService.calculateFinesForOverdueLoans();

        ArgumentCaptor<Fine> fineArgumentCaptor = ArgumentCaptor.forClass(Fine.class);
        verify(fineRepository).save(fineArgumentCaptor.capture());
        Fine capturedFine = fineArgumentCaptor.getValue();

        ArgumentCaptor<Loan> loanArgumentCaptor = ArgumentCaptor.forClass(Loan.class);
        verify(loanRepository).save(loanArgumentCaptor.capture());
        Loan capturedLoan = loanArgumentCaptor.getValue();

        assertThat(capturedFine).isEqualTo(fine);
        assertThat(capturedLoan).isEqualTo(loan);
    }

    @Test
    void canConvertEntityToDto() {
        Fine fine = new Fine ();
        Loan loan = new Loan();
        loan.setStatus(LoanStatus.ACTIVE);
        fine.setStatus(FineStatus.UNPAID);
        fine.setDateTimeOfFine(LocalDateTime.now());
        fine.setAmount(1.0);
        fine.setLoan(loan);

        LoanDto loanDto = new LoanDto();
        loanDto.setStatus(LoanStatus.ACTIVE);

        when(loanService.convertEntityToDto(loan)).thenReturn(loanDto);
        FineDto fineDto = fineService.convertEntityToDto(fine);

        assertThat(fineDto.getAmount()).isEqualTo(fine.getAmount());
        assertThat(fineDto.getLoan().getLoan_id()).isEqualTo(fine.getLoan().getLoan_id());
        assertThat(fineDto.getLoan().getStatus()).isEqualTo(fine.getLoan().getStatus());
        assertThat(fineDto.getStatus()).isEqualTo(fine.getStatus());
        assertThat(fineDto.getDateTimeOfFine()).isEqualTo(fine.getDateTimeOfFine());
        assertThat(fineDto.getFine_id()).isEqualTo(fine.getFine_id());

    }

    @Test
    void canSearchFines() {
        int pageSize = 5;
        int pageNo = 1;
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

        String query = "1";
        String searchBy = "fine";
        String status = "PAID";
        FineStatus statusFilter = FineStatus.PAID;
        String sort = "fine_id";
        String order = "asc";

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "fine_id");

        when(fineRepository.searchFinesWithStatusAndNotAny(query, statusFilter, searchBy, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));


        fineService.searchFines(query, pageable, Optional.empty(), IdType.NONE, status, searchBy, sort ,order);
        verify(fineRepository).searchFinesWithStatusAndNotAny(query, statusFilter, searchBy, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));
    }

    @Test
    void canFindPaginated() {
        int pageSize = 5;
        int pageNo = 1;
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

        when(fineRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        fineService.findPaginated(pageNo, pageSize, Optional.empty(), IdType.NONE);


        verify(fineRepository).findAll(pageable);
    }

    @Test
    void canFindById() {
        fineService.findById((long)1);
        verify(fineRepository).findById((long)1);
    }

    @Test
    void canUpdateFines() {
        Fine fine = new Fine ();
        fine.setStatus(FineStatus.UNPAID);
        fine.setAmount(1.0);
        fine.setDateTimeOfFine(LocalDateTime.now());
        fine.setFine_id(1L);

        Loan loan = new Loan();
        fine.setLoan(loan);

        FineDto fineDto = new FineDto();
        fineDto.setStatus(FineStatus.PAID);
        when(fineRepository.findById(1L)).thenReturn(Optional.of(fine));
        fineService.updateFines(fineDto, 1L);

        ArgumentCaptor <Fine> fineArgumentCaptor = ArgumentCaptor.forClass(Fine.class);
        verify(fineRepository).save(fineArgumentCaptor.capture());
        Fine capturedFine = fineArgumentCaptor.getValue();

        assertThat(capturedFine.getStatus()).isEqualTo(fineDto.getStatus());
    }

    @Test
    void canDeleteFines() {
        Fine fine = new Fine();
        when(fineRepository.findById((long)1))
                .thenReturn(Optional.of(fine));


        fineService.deleteFines((long)1);
        ArgumentCaptor <Fine> fineArgumentCaptor = ArgumentCaptor.forClass(Fine.class);
        verify(fineRepository).save(fineArgumentCaptor.capture());
        Fine capturedFine = fineArgumentCaptor.getValue();

        assertThat(capturedFine.isDeleted()).isEqualTo(true);
    }

    @Test
    void canCalculateTotalFines() {
        Fine fine = new Fine();
        fine.setAmount(2.0);
        Member member = new Member();
        member.setMember_id(1L);
        Book book = new Book();
        Loan loan = new Loan ();
        loan.setMember(member);
        loan.setBook(book);
        fine.setLoan(loan);

        Fine fineTwo = new Fine();
        fineTwo.setAmount(3.0);
        fineTwo.setLoan(loan);

        when(fineRepository.findAll()).thenReturn(Arrays.asList(fine, fineTwo));

        double sum = fineService.calculateTotalFines((long)1);
        assertThat(sum).isEqualTo(5.0);
    }

    @Test
    void canFindFinesByMemberId() {
        Fine fine = new Fine();
        fine.setAmount(2.0);
        Member member = new Member();
        member.setMember_id(1L);
        Book book = new Book();
        Loan loan = new Loan ();
        loan.setMember(member);
        loan.setBook(book);
        fine.setLoan(loan);

        when(fineRepository.findAll()).thenReturn(Arrays.asList(fine));

        List<FineDto> result = fineService.findFinesByMemberId(1L);

        assertEquals(1L, result.size());
        assertEquals(2.0, result.get(0).getAmount());
    }
}