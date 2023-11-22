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
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FineServiceImpl implements FineService {
    private final LoanRepository loanRepository;
    private final FineRepository fineRepository;

    private final MemberRepository memberRepository;
    private final MemberService memberService;

    private final LoanService loanService;

    // Define the fine rate (e.g., $1 per day)
    private static final double FINE_RATE_PER_DAY = 1.0;

    public FineServiceImpl(LoanRepository loanRepository, FineRepository fineRepository, MemberRepository memberRepository, MemberService memberService,
                           LoanService loanService) {
        this.loanRepository = loanRepository;
        this.fineRepository = fineRepository;
        this.memberRepository = memberRepository;
        this.memberService = memberService;
        this.loanService = loanService;
    }

    List<FineDto> findAllFines (){
        List<Fine> fines = fineRepository.findAll();
        return fines.stream().map((fine) -> convertEntityToDto(fine))
                .collect(Collectors.toList());
    }

    @Scheduled(cron = "0/10 * * * * *") //run every 10 sec
    public void calculateFinesForOverdueLoans() {
        LocalDateTime today = LocalDateTime.now();

        List<Loan> allOverdueLoans = loanRepository.findOverdueLoansByDueDateTimeBefore(today);


        // Query the database for overdue loans
        List<Loan> overdueLoans = allOverdueLoans.stream()
                .filter(loan -> loan.getStatus() == LoanStatus.ACTIVE || loan.getStatus() == LoanStatus.OVERDUE)
                .collect(Collectors.toList());

        for (Loan loan : overdueLoans) {
            // Calculate the number of days overdue
            long daysOverdue = ChronoUnit.DAYS.between(loan.getDueDateTime(), today);

            // Calculate the fine amount
            double fineAmount = daysOverdue * FINE_RATE_PER_DAY;

            // Check if a fine already exists for this loan
            List<Fine> finesForLoan = fineRepository.findByLoan(loan);

            if (!finesForLoan.isEmpty()) {
                // Update the existing fine
                Fine existingFine = finesForLoan.get(0); // Assuming there is only one fine per loan
                existingFine.setAmount(fineAmount);
                existingFine.setDateTimeOfFine(today);
                existingFine.setStatus(FineStatus.UNPAID);

                fineRepository.save(existingFine);
            } else {
                // Create a new Fine entity and save it
                Fine newFine = new Fine();
                newFine.setLoan(loan);
                newFine.setAmount(fineAmount);
                newFine.setDateTimeOfFine(today);
                newFine.setStatus(FineStatus.UNPAID);

                fineRepository.save(newFine);
            }

            // Update the loan with the fine amount
            loan.setFineAmount(fineAmount);
            loan.setDaysOverdue(daysOverdue);
            loan.setStatus(LoanStatus.OVERDUE);
            loanRepository.save(loan);
        }
    }

    @Override
    public FineDto convertEntityToDto(Fine fine) {
        FineDto fineDto = new FineDto();
        fineDto.setFine_id(fine.getFine_id());
        fineDto.setAmount(fine.getAmount());
        fineDto.setDateTimeOfFine(fine.getDateTimeOfFine());


        // Convert book and member entities to DTOs
        LoanDto loanDto = loanService.convertEntityToDto(fine.getLoan());
//        MemberDto memberDto = memberService.convertEntityToDto(fine.getMember());

        fineDto.setLoan(loanDto);
//        fineDto.setMember(memberDto);

        // Set the status here based on the loan's status
        if (fine.getStatus() == FineStatus.PAID) {
            fineDto.setStatus(FineStatus.PAID);
        } else if (fine.getStatus() == FineStatus.UNPAID) {
            fineDto.setStatus(FineStatus.UNPAID);
        }
        return fineDto;
    }

    @Override
    public Page<FineDto> searchFines(String query, Pageable pageable, Optional<Long> id, IdType idType,
                                     String statusFilter, String searchBy, String sort, String order) {
        Page<Fine> selectedFines = fineRepository.findAll(pageable);

        FineStatus status = FineStatus.UNPAID;
        if (statusFilter.equals("ALL")){
            status = null;
        }
        else if (statusFilter.equals("PAID")){
            status = FineStatus.PAID;
        }
        else if (statusFilter.equals("UNPAID")){
            status = FineStatus.UNPAID;
        }

        Sort.Direction direction = Sort.Direction.ASC;

        if (order.equals("desc")){
            direction = Sort.Direction.DESC;
        }

        Sort sortable = Sort.by(direction, "fine_id"); // Default sorting by title



        if (sort != null) {
            switch (sort) {
                case "loanId":
                    sortable = Sort.by(direction, "loan.loan_id");
                    break;
                case "bookId":
                    sortable = Sort.by(direction, "loan.book.book_id");
                    break;
                case "title":
                    sortable = Sort.by(direction, "loan.book.title");
                    break;
                case "fine":
                    sortable = Sort.by(direction, "amount");
                    break;
            }
        }

        if (id.isPresent()) {
            Long selectedId = id.get();

            if (idType == IdType.MEMBER_ID) {
                Member member = memberRepository.findById(selectedId).get();
                if (searchBy.equals("any")) {
                    selectedFines = fineRepository.searchFinesByMemberWithStatusAndByAny(query, status, selectedId, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));
                }
                else {
                    selectedFines = fineRepository.searchFinesByMemberWithStatusAndNotAny(query, status, searchBy,selectedId, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));
                }

            }
        }
        else {
            if (searchBy.equals("any")){
                selectedFines = fineRepository.searchFinesWithStatusAndByAny(query, status, pageable);
            }
            else {
                selectedFines = fineRepository.searchFinesWithStatusAndNotAny(query, status, searchBy, pageable);
            }
        }

        List<FineDto> matchedFines = selectedFines.getContent().stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());


        return new PageImpl<>(matchedFines, pageable, selectedFines.getTotalElements());
    }


    @Override
    public Page<FineDto> findPaginated(int pageNo, int pageSize, Optional<Long> id, IdType idType) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<Fine> selectedFines = this.fineRepository.findAll(pageable);
        if (id.isPresent()){
            Long selectedId = id.get();

            if (idType == IdType.MEMBER_ID){
                Member member = memberRepository.findById(selectedId).get();
                selectedFines = this.fineRepository.findAllByLoan_Member(member, pageable);
            }
        }

        List<FineDto> matchedFines = selectedFines.stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(matchedFines, pageable, selectedFines.getTotalElements());
    }

    @Override
    public Optional<FineDto> findById(Long id){
        Optional<Fine>fine = fineRepository.findById(id);
        return fine.map(this::convertEntityToDto);
    }

    @Override
    public void updateFines(FineDto fine, Long fineId) {
        Optional<Fine> selectedFine = fineRepository.findById(fineId);

        if (selectedFine.isPresent()){
            Fine existingFine = selectedFine.get();

            existingFine.setStatus(fine.getStatus());

            fineRepository.save(existingFine);
        }

    }

    @Override
    public void deleteFines(Long id){
            Fine fineToDelete = fineRepository.findById(id).orElse(null);

        if (fineToDelete != null) {
            // Delete the loan
            fineRepository.delete(fineToDelete);
        }
    }

    @Override
    public Double calculateTotalFines(Long id) {
        List<Fine> fineList = fineRepository.findAll();

        List<FineDto> matchedFines = fineList.stream().filter(fine -> fine.getLoan().getMember().getMember_id().equals(id) && fine.getStatus().equals(FineStatus.UNPAID))
                .map(this::convertEntityToDto).collect(Collectors.toList());


        double sum = 0;
        for (FineDto fine : matchedFines) {
            sum += fine.getAmount();
        }

        return sum;
    }

    @Override
    public List<FineDto> findFinesByMemberId (Long memberId){
        List<Fine> fines = fineRepository.findAll();
        return fines.stream().filter(fine -> fine.getLoan().getMember().getMember_id().equals(memberId))
                .map(this::convertEntityToDto).collect(Collectors.toList());
    }
}
