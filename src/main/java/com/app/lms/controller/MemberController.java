package com.app.lms.controller;

import com.app.lms.entity.FineStatus;
import com.app.lms.entity.IdType;
import com.app.lms.entity.LoanStatus;
import com.app.lms.entity.Member;
import com.app.lms.service.*;
import com.app.lms.web.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class MemberController {

    private final BookService bookService;
    private final LoanService loanService;
    private final FineService fineService;
    private final MemberService memberService;
    private final PaymentService paymentService;

    public MemberController(BookService bookService, LoanService loanService, FineService fineService,
                            MemberService memberService, PaymentService paymentService) {
        this.bookService = bookService;
        this.loanService = loanService;
        this.fineService = fineService;
        this.memberService = memberService;
        this.paymentService = paymentService;
    }

    // Provides the current user's information to be used in the navbar.
    @ModelAttribute("currentUser")
    public MemberDto navbar(Principal principal) {
        String username = principal.getName();
        return memberService.findByEmail(username);
    }


    /*--------------HOME/LANDING PAGE AFTER LOGIN----------------*/

    // Home page
    @GetMapping("/home")
    public String start(Model model, Principal principal) {

        String username = principal.getName();
        MemberDto member = memberService.findByEmail(username);

        // Finding the count of active, overdue, all loans, and for fine
        Long allActiveLoansCount = loanService.findAllActiveLoansByIdCount(member);
        Long allOverdueLoansCount = loanService.findAllOverdueLoansByIdCount(member);
        Long allLoansCount = loanService.findLoansByMemberCount(member);
        Double fineCount = fineService.calculateTotalFines(member.getMember_id());

        // Retrieve all books from the repository
        List<BookDto> allBooks = bookService.findAllBooks();

        // Shuffle the list to randomize the order
        Collections.shuffle(allBooks);

        // Create a new list to store the first 4 distinct (non-duplicate) books
        List<BookDto> randomBooks = new ArrayList<>();

        for (BookDto book : allBooks) {
            if (randomBooks.size() >= 4) {
                break; // We have enough random books
            }

            // Check if the book is not already in the randomBooks list
            if (!randomBooks.contains(book)) {
                randomBooks.add(book);
            }
        }

        // Add the randomBooks list to the model for rendering in the view
        model.addAttribute("fineCount", fineCount);
        model.addAttribute("randomBooks", randomBooks);
        model.addAttribute("allActiveLoansCount", allActiveLoansCount);
        model.addAttribute("allOverdueLoansCount", allOverdueLoansCount);
        model.addAttribute("allLoansCount", allLoansCount);
        return "home";
    }


    /*--------------BOOKS----------------*/

    // Retrieve pages for all books. If search filter is detected, searchBooks is used
    @GetMapping("/allbooks")
    public String findPaginatedAllBooks(@RequestParam(name = "page", defaultValue = "1") int pageNo,
                                        @RequestParam(name = "query", required = false) String query,
                                        @RequestParam(name = "searchBy", required = false) String searchBy,
                                        @RequestParam(name = "statusFilter", required = false) String statusFilter,
                                        @RequestParam(name = "sort", required = false) String sort,
                                        @RequestParam(name = "order", required = false) String order,
                                        Model model){
        int pageSize = 9;
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<BookDto> page;
        if ((query != null && !query.isEmpty()) || searchBy != null || statusFilter != null)  {
            page = bookService.searchBooks(query, pageable, searchBy, statusFilter, sort, order);
        } else {
            // Otherwise, retrieve paginated members
            page = bookService.findPaginated(pageNo, pageSize);
        }

        List<BookDto> listBooks = page.getContent();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("books", listBooks);
        model.addAttribute("query", query);
        model.addAttribute("searchBy", searchBy);
        model.addAttribute("statusFilter", statusFilter);
        model.addAttribute("sort", sort);
        model.addAttribute("order", order);
        return "books";
    }


    /*--------------LOANS----------------*/

    // Retrieve pages for loan history. If search filter is detected, searchLoans is used
    @GetMapping("/loanhistory")
    public String showLoanHistory(Principal principal,
                                  @RequestParam(name = "page", defaultValue = "1") int pageNo,
                                  @RequestParam(name = "query", required = false) String query,
                                  @RequestParam(name = "statusFilter", required = false) String statusFilter,
                                  @RequestParam(name = "searchBy", required = false) String searchBy,
                                  @RequestParam(name = "sort", required = false) String sort,
                                  @RequestParam(name = "order", required = false) String order,
                                  Model model) {
        String username = principal.getName();
        MemberDto member = memberService.findByEmail(username);

        int pageSize = 5;
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<LoanDto> page;

        if ((query != null && !query.isEmpty()) || statusFilter != null){
            page = loanService.searchLoans(query, pageable, Optional.of(member.getMember_id()), IdType.MEMBER_ID, statusFilter, searchBy, sort, order);
        } else {
            // Otherwise, retrieve paginated members
            page = loanService.findPaginated(pageNo, pageSize, Optional.of(member.getMember_id()), IdType.MEMBER_ID);
        }

        List<LoanDto> listLoans = page.getContent();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("loans", listLoans);
        model.addAttribute("query", query);
        model.addAttribute("statusFilter", statusFilter);
        model.addAttribute("searchBy", searchBy);
        model.addAttribute("sort", sort);
        model.addAttribute("order", order);
        return "loanHistory";
    }


    /*--------------FINES----------------*/

    // Retrieve pages for fines. If search filter is detected, searchFines is used
    @GetMapping("/fines")
    public String showFines(@RequestParam(name = "page", defaultValue = "1") int pageNo,
                              @RequestParam(name = "query", required = false) String query,
                            @RequestParam(name = "searchBy", required = false) String searchBy,
                            @RequestParam(name = "statusFilter", required = false) String statusFilter,
                            @RequestParam(name = "sort", required = false) String sort,
                            @RequestParam(name = "order", required = false) String order,
                            Model model, Principal principal) {
        int pageSize = 5;
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<FineDto> page;

        String username = principal.getName();
        MemberDto member = memberService.findByEmail(username);

        if ((query != null && !query.isEmpty()) || statusFilter != null){
            page = fineService.searchFines(query, pageable, Optional.of(member.getMember_id()),
                    IdType.MEMBER_ID, statusFilter, searchBy, sort, order);
        } else {
            // Otherwise, retrieve paginated members
            page = fineService.findPaginated(pageNo, pageSize, Optional.of(member.getMember_id()), IdType.MEMBER_ID);
        }

        List<FineDto> listFines = page.getContent();
//        fineService.calculateFinesForOverdueLoans();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("fines", listFines);
        model.addAttribute("query", query);
        model.addAttribute("statusFilter", statusFilter);
        model.addAttribute("searchBy", searchBy);
        model.addAttribute("sort", sort);
        model.addAttribute("order", order);
        return "fines";
    }


    /*--------------PAYMENT----------------*/

    // Retrieve payment page
    @GetMapping("/payment")
    public String showPayment (Principal principal, Model model){
        String username = principal.getName();
        MemberDto member = memberService.findByEmail(username);
        List <FineDto> fineList = fineService.findFinesByMemberId(member.getMember_id())
                .stream().filter(fine -> fine.getStatus().equals(FineStatus.UNPAID) && fine.getLoan().getStatus().equals(LoanStatus.RETURNED))
                .collect(Collectors.toList());

        model.addAttribute("fines", fineList);
        return "payment";
    }

    // Retrieve payment checkout page
    @PostMapping("/payment/checkout")
    public String confirmPayment (Principal principal, Model model,
                           @RequestParam(value = "fine_ids", required = true) String fine_ids){
        String[] fineIdsArray = fine_ids.split(",");
        String username = principal.getName();
        MemberDto member = memberService.findByEmail(username);
        List <FineDto> fineList = fineService.findFinesByMemberId(member.getMember_id());
        List<FineDto> selectedFines = fineList.stream()
                .filter(fine -> Arrays.stream(fineIdsArray).anyMatch(id -> fine.getFine_id().equals(Long.parseLong(id))))
                .collect(Collectors.toList());

        Double totalFineAmount = selectedFines.stream()
                .mapToDouble(fine -> fine.getAmount()) // Assuming FineDto has a method to get the amount, replace it with the actual method
                .sum();

        model.addAttribute("confirmedFines", fine_ids);
        model.addAttribute("totalFineAmount", totalFineAmount);
        model.addAttribute("selectedFines", selectedFines);
        return "makePayment";
    }

    // For user to make payment, before directing to a results page
    @PostMapping("/payment/checkout/pay")
    public String paymentType (Principal principal, Model model,
                               @RequestParam(value = "confirmedFines", required = true) String fine_ids,
                               @RequestParam(value = "status", required = false) String paymentStatus){
        String[] fineIdsArray = fine_ids.split(",");
        String username = principal.getName();
        MemberDto member = memberService.findByEmail(username);
        List <FineDto> fineList = fineService.findFinesByMemberId(member.getMember_id());
        List<FineDto> selectedFines = fineList.stream()
                .filter(fine -> Arrays.stream(fineIdsArray).anyMatch(id -> fine.getFine_id().equals(Long.parseLong(id))))
                .collect(Collectors.toList());

        Double totalFineAmount = selectedFines.stream()
                .mapToDouble(fine -> fine.getAmount()) // Assuming FineDto has a method to get the amount, replace it with the actual method
                .sum();

        if (paymentStatus.equals("processing")){
            paymentService.makePayment(selectedFines, totalFineAmount, "Credit/Debit Card", username);
            PaymentDto paymentDto = paymentService.getPaymentsByUser(member.getMember_id());
            model.addAttribute("payment", paymentDto);
            return "paymentSuccess";
        }
        else{
            model.addAttribute("totalFineAmount", totalFineAmount);
            model.addAttribute("confirmedFines", fine_ids);
            model.addAttribute("selectedFines", selectedFines);
            return "paymentType";
        }
    }

    // Retrieve pages for payment history. If search filter is detected, searchPayments is used
    @GetMapping("/paymenthistory")
    public String showPaymentHistory(Principal principal,
                                  @RequestParam(name = "page", defaultValue = "1") int pageNo,
                                  @RequestParam(name = "query", required = false) String query,
                                     @RequestParam(name = "searchBy", required = false) String searchBy,
                                     @RequestParam(name = "statusFilter", required = false) String statusFilter,
                                     @RequestParam(name = "sort", required = false) String sort,
                                     @RequestParam(name = "order", required = false) String order,
                                     Model model) {
        String username = principal.getName();
        MemberDto member = memberService.findByEmail(username);

        int pageSize = 5;
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<PaymentDto> page;
        if ((query != null && !query.isEmpty()) || statusFilter != null || searchBy != null){
            page = paymentService.searchPayments(query, pageable, Optional.of(member.getMember_id()),
                    IdType.MEMBER_ID, statusFilter, searchBy, sort, order);
        } else {
            // Otherwise, retrieve paginated members
            page = paymentService.findPaginated(pageNo, pageSize, Optional.of(member.getMember_id()), IdType.MEMBER_ID);
        }

        List<PaymentDto> listPayments = page.getContent();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("payments", listPayments);
        model.addAttribute("query", query);
        model.addAttribute("statusFilter", statusFilter);
        model.addAttribute("searchBy", searchBy);
        model.addAttribute("sort", sort);
        model.addAttribute("order", order);
        return "paymentHistory";
    }


    /*--------------ACCOUNT SETTINGS----------------*/

    // Retrieve the account setting page
    @GetMapping("/accountsettings")
    public String showAccountSettings(Principal principal, Model model){
        String username = principal.getName();
        MemberDto member = memberService.findByEmail(username);
        model.addAttribute("member", member);
        return "member/accountSettings/accountSettings";
    }

    // Retrieve the update name page
    @GetMapping("/accountsettings/updatename")
    public String showUpdateName (Principal principal, Model model){
        String username = principal.getName();
        MemberDto member = memberService.findByEmail(username);
        model.addAttribute("member", member);
        return "member/accountSettings/updateName";
    }

    // Update/Save any changes made to name
    @PutMapping("/accountsettings/updatename/save")
    public String updateName (@ModelAttribute("member") MemberDto memberDto, Principal principal){
        String username = principal.getName();
        memberService.updateName(memberDto, username);
        return "redirect:/accountsettings?success";
    }

    // Retrieve the update email page
    @GetMapping("/accountsettings/updateemail")
    public String showUpdateEmail (Principal principal, Model model){
        String username = principal.getName();
        MemberDto member = memberService.findByEmail(username);
        model.addAttribute("member", member);
        return "member/accountSettings/updateEmail";
    }

    // Update/Save any changes made to email
    @PutMapping("/accountsettings/updateemail/save")
    public String updateEmail (@ModelAttribute("member") MemberDto memberDto, Principal principal){
        String username = principal.getName();
        memberService.updateEmail(memberDto, username);
        return "redirect:/logout";
    }

    // Retrieve the update gender page
    @GetMapping("/accountsettings/updategender")
    public String showUpdateGender (Principal principal, Model model){
        String username = principal.getName();
        MemberDto member = memberService.findByEmail(username);
        model.addAttribute("member", member);
        return "member/accountSettings/updateGender";
    }

    // Update/Save any changes made to gender
    @PutMapping("/accountsettings/updategender/save")
    public String updateGender (@ModelAttribute("member") MemberDto memberDto, Principal principal){
        String username = principal.getName();
        memberService.updateGender(memberDto, username);
        return "redirect:/accountsettings?success";
    }

    // Retrieve the update address 1 page
    @GetMapping("/accountsettings/updateaddressone")
    public String showUpdateAddressOne (Principal principal, Model model){
        String username = principal.getName();
        MemberDto member = memberService.findByEmail(username);
        model.addAttribute("member", member);
        return "member/accountSettings/updateAddressOne";
    }

    // Update/Save any changes made to address 1
    @PutMapping("/accountsettings/updateaddressone/save")
    public String updateAddressOne (@ModelAttribute("member") MemberDto memberDto, Principal principal){
        String username = principal.getName();
        memberService.updateAddressOne(memberDto, username);
        return "redirect:/accountsettings?success";
    }

    // Retrieve the update address 2 page
    @GetMapping("/accountsettings/updateaddresstwo")
    public String showUpdateAddressTwo (Principal principal, Model model){
        String username = principal.getName();
        MemberDto member = memberService.findByEmail(username);
        model.addAttribute("member", member);
        return "member/accountSettings/updateAddressTwo";
    }

    // Update/Save any changes made to address 2
    @PutMapping("/accountsettings/updateaddresstwo/save")
    public String updateAddressTwo (@ModelAttribute("member") MemberDto memberDto, Principal principal){
        String username = principal.getName();
        memberService.updateAddressTwo(memberDto, username);
        return "redirect:/accountsettings?success";
    }

    // Retrieve the update postal code page
    @GetMapping("/accountsettings/updatepostalcode")
    public String showUpdatePostalCode (Principal principal, Model model){
        String username = principal.getName();
        MemberDto member = memberService.findByEmail(username);
        model.addAttribute("member", member);
        return "member/accountSettings/updatePostalCode";
    }

    // Update/Save any changes made to postal code
    @PutMapping("/accountsettings/updatepostalcode/save")
    public String updatePostalCode (@ModelAttribute("member") MemberDto memberDto, Principal principal){
        String username = principal.getName();
        memberService.updatePostalCode(memberDto, username);
        return "redirect:/accountsettings?success";
    }

    // Retrieve the update password page
    @GetMapping("/accountsettings/updatepassword")
    public String showUpdatePassword (Principal principal, Model model){
        String username = principal.getName();
        MemberDto member = memberService.findByEmail(username);
        model.addAttribute("member", member);
        return "member/accountSettings/updatePassword";
    }

    // Update/Save any changes made to password
    @PutMapping("/accountsettings/updatepassword/save")
    public String updatePassword (@ModelAttribute("member") MemberDto memberDto,
                                  @RequestParam("password") String password,
                                  @RequestParam("newPassword") String newPassword,
                                  BindingResult result,
                                  Principal principal, Model model){
        String username = principal.getName();
        boolean checkPassword = memberService.checkPassword(password, username);

        if (!checkPassword) {
            result.rejectValue("password", null, "Password is incorrect");
        }

        if (result.hasErrors()) {
            model.addAttribute("member", memberDto);
            return "member/accountSettings/updatePassword";
        }

        memberService.updatePassword(newPassword, username);
        return "redirect:/accountsettings?success";
    }
}
