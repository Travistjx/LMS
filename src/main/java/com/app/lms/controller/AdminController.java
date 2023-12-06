package com.app.lms.controller;

import com.app.lms.entity.BookStatus;
import com.app.lms.entity.Member;
import com.app.lms.service.*;
import com.app.lms.entity.IdType;
import com.app.lms.web.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/adminportal")
public class AdminController {

    private final MemberService memberService;
    private final BookService bookService;

    private final LoanService loanService;

    private final FineService fineService;

    private final PaymentService paymentService;

    public AdminController(MemberService memberService, BookService bookService, LoanService loanService,
                           FineService fineService, PaymentService paymentService) {
        this.memberService = memberService;
        this.bookService = bookService;
        this.loanService = loanService;
        this.fineService = fineService;
        this.paymentService = paymentService;
    }

    // Provides the current user's information to be used in the navbar.
    @ModelAttribute("currentUser")
    public MemberDto navbar(Principal principal) {
        String username = principal.getName();
        return memberService.findByEmail(username);
    }

    // Landing page
    @GetMapping("")
    public String homePage(Model model) {
        List <MemberDto> allMembers = memberService.findAllUsers();
        List<MemberDto> last4Members = allMembers.subList(Math.max(0, allMembers.size() - 4), allMembers.size());
        int totalMembers = allMembers.size();

        List <BookDto> allBooks = bookService.findAllBooks();
        List <BookDto> last4Books = allBooks.subList(Math.max(0, allBooks.size() - 4), allBooks.size());
        int totalBooks = allBooks.size();

        Long activeLoans = loanService.findAllActiveLoansCount();
        Long overdueLoans = loanService.findAllOverdueLoansCount();

        model.addAttribute("members", last4Members);
        model.addAttribute("books", last4Books);
        model.addAttribute("totalBooks", totalBooks);
        model.addAttribute("totalMembers", totalMembers);
        model.addAttribute("activeLoans", activeLoans);
        model.addAttribute("overdueLoans", overdueLoans);
        return "adminportal";
    }

    /*--------------BOOKS------------- */

    // Direct to add book page
    @GetMapping("/addbooks")
    public String showAddBooksForm(Model model) {
        BookDto book = new BookDto();
        model.addAttribute("book", book);
        return "addbooks";
    }

    // Retrieve Update Books page based on Book Id
    @GetMapping("/updatebooks/{id}")
    public String showUpdateBookForm(@PathVariable(value = "id") Long id, Model model) {
        Optional<BookDto> bookOptional = bookService.findById(id);
        if (bookOptional.isPresent()) {
            BookDto book = bookOptional.get();
            model.addAttribute("book", book);

            String authors = book.getAuthors().stream()
                    .map(author -> author.getFirstName() + ' ' + author.getLastName())
                    .collect(Collectors.joining(", "));
            model.addAttribute("authors", authors);
        } else {
            return "managebooks";
        }
        return "updatebooks";
    }

    // Save (new) changes for book based on update
    @PutMapping("/updatebooks/{id}/save")
    public String updateBooks(@ModelAttribute("book") BookDto book,
                              @RequestParam("imageFile") MultipartFile file,
                              @RequestParam("currentAuthors") String authors,
                              @PathVariable(value = "id") Long id) {
        bookService.updateBooks(book, id, file, authors);
        return "redirect:/adminportal/updatebooks/{id}?success";
    }

    // Delete Books
    @DeleteMapping("/managebooks/{id}")
    public String deleteBooks(@PathVariable(value = "id") Long id) {
        bookService.deleteBooks(id);
        return "redirect:/adminportal/managebooks?success";
    }

    // Add/Save the new book
    @PostMapping("/addbooks/save")
    public String saveBooks(@ModelAttribute("book") BookDto book,
                            @RequestParam("imageFile") MultipartFile file,
                            BindingResult result,
                            Model model) {
        bookService.saveBook(book, file);
        return "redirect:/adminportal/addbooks?success";
    }

    // Retrieve pages for books. If search filter is detected, searchBooks is used
    @GetMapping("/managebooks")
    public String findPaginatedManageBooks(@RequestParam(name = "page", defaultValue = "1") int pageNo,
                                           @RequestParam(name = "query", required = false) String query,
                                           @RequestParam(name = "searchBy", required = false) String searchBy,
                                           @RequestParam(name = "statusFilter", required = false) String statusFilter,
                                           @RequestParam(name = "sort", required = false) String sort,
                                           @RequestParam(name = "order", required = false) String order,
                                           Model model) {
        int pageSize = 5;
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
        return "managebooks";
    }


    /*--------------ACCOUNTS-------------- */

    // Retrieve create account page
    @GetMapping("/createaccount")
    public String createAccount(Model model) {
        MemberDto member = new MemberDto();
        model.addAttribute("member", member);
        return "createaccount";
    }

    // Handler method to handle create account form submit request
    // Create/Save new account
    @PostMapping("/createaccount/save")
    public String registration(@ModelAttribute("member") MemberDto member,
                               BindingResult result,
                               Model model) {
        MemberDto existing = memberService.findByEmail(member.getEmail());
        if (existing != null) {
            result.rejectValue("email", null, "There is already an account registered with that email");
        }
        if (result.hasErrors()) {
            model.addAttribute("member", member);
            return "createaccount";
        }
        memberService.saveUser(member);
        return "redirect:/adminportal/createaccount?success";
    }

    // Retrieve update accounts page based on Account Id
    @GetMapping("/updateaccounts/{id}")
    public String showUpdateAccountForm(@PathVariable(value = "id") Long id, Model model) {
        Optional<MemberDto> memberOptional = memberService.findById(id);
        if (memberOptional.isPresent()) {
            MemberDto member = memberOptional.get();
            model.addAttribute("member", member);

            String roles = member.getRoles().stream()
                    .map(role -> role.getName())
                    .collect(Collectors.joining(", "));
            model.addAttribute("roles", roles);
        } else {
            return "manageaccounts";
        }
        return "updateaccounts";
    }

    // Update/Save changes to account
    @PutMapping("/updateaccounts/{id}/save")
    public String updateAccounts(@ModelAttribute("member") MemberDto member,
                                 @RequestParam("currentRoles") String roles,
                                 @PathVariable(value = "id") Long id) {
        memberService.updateAccounts(member, id, roles);
        return "redirect:/adminportal/updateaccounts/{id}?success";
    }

    // Delete Account
    @DeleteMapping("/manageaccounts/{id}")
    public String deleteAccounts(@PathVariable(value = "id") Long id) {
        memberService.deleteMembers(id);
        return "redirect:/adminportal/manageaccounts?success";
    }

    // Retrieve pages for accounts. If search filter is detected, searchMembers is used
    @GetMapping("/manageaccounts")
    public String findPaginatedManageAccounts(@RequestParam(name = "page", defaultValue = "1") int pageNo,
                                              @RequestParam(name = "query", required = false) String query,
                                              @RequestParam(name = "searchBy", required = false) String searchBy,
                                              @RequestParam(name = "statusFilter", required = false) String statusFilter,
                                              @RequestParam(name = "sort", required = false) String sort,
                                              @RequestParam(name = "order", required = false) String order,
                                              Model model) {
        int pageSize = 5;
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<MemberDto> page;
        if ((query != null && !query.isEmpty()) || statusFilter != null || searchBy != null) {
            page = memberService.searchMembers(query, pageable, statusFilter, searchBy, sort, order);
        } else {
            // Otherwise, retrieve paginated members
            page = memberService.findPaginated(pageNo, pageSize);
        }

        List<MemberDto> listMembers = page.getContent();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("members", listMembers);
        model.addAttribute("query", query);
        model.addAttribute("statusFilter", statusFilter);
        model.addAttribute("searchBy", searchBy);
        model.addAttribute("sort", sort);
        model.addAttribute("order", order);
        return "manageaccounts";
    }


    /*--------------LOANS------------- */

    // Retrieve issue books page
    @GetMapping("/issuebooks")
    public String showIssueBooksForm(Model model) {
        List<BookDto> allBooks = bookService.findAllBooks().stream().filter(bookDto ->
                bookDto.getStatus() == BookStatus.AVAILABLE).collect(Collectors.toList());
        List<MemberDto> allMembers = memberService.findAllUsers();
        model.addAttribute("loan", new LoanDto());
        model.addAttribute("allBooks", allBooks);
        model.addAttribute("allMembers", allMembers);
        return "issuebooks";
    }

    // Issue the books and save the loan in the database
    @PostMapping("/issuebooks/save")
    public String issueBook(@ModelAttribute("loan") LoanDto loan, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "issuebooks"; // Return to the form with errors
        }

        loanService.issueBooks(loan);

        return "redirect:/adminportal/issuebooks?success"; // Redirect to the form with a success message
    }

    // Retrieve the return books page
    @GetMapping("/returnbooks")
    public String showReturnBooksForm(Model model) {
        List<BookDto> allBooks = bookService.findAllBooks();
        List<MemberDto> allMembers = memberService.findAllUsers();
        model.addAttribute("loan", new LoanDto());
        model.addAttribute("allBooks", allBooks);
        model.addAttribute("allMembers", allMembers);
        return "returnbooks";
    }

    // Retrieve any existing loan based on search (Book Id and Member Id)
    @GetMapping("/returnbooks/search")
    public String searchLoans(@RequestParam(name = "searchBookId", required = false) Long bookId,
                              @RequestParam(name = "searchMemberId", required = false) Long memberId,
                              Model model) {
        // Call the service method to search for loans based on bookId and memberId
        List<LoanDto> existingLoans = loanService.searchLoans(bookId, memberId);

        // Add the search results to the model
        model.addAttribute("existingLoans", existingLoans);

        return "returnSearchResults";
    }

    // return the books and update the loan status
    @PutMapping("/returnbooks/return/{id}")
    public String returnBooks(@PathVariable(value = "id") Long id) {
        // Call the service method to search for loans based on bookId and memberId
        loanService.returnBooks(id);
        return "redirect:/adminportal/returnbooks?success";
    }

    // Retrieve pages for loans. If search filter is detected, searchLoans is used
    @GetMapping("/manageloans")
    public String manageLoans(@RequestParam(name = "page", defaultValue = "1") int pageNo,
                                              @RequestParam(name = "query", required = false) String query,
                              @RequestParam(name = "searchBy", required = false) String searchBy,
                              @RequestParam(name = "statusFilter", required = false) String statusFilter,
                              @RequestParam(name = "sort", required = false) String sort,
                              @RequestParam(name = "order", required = false) String order,
                              Model model) {
        int pageSize = 5;
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<LoanDto> page;
        if ((query != null && !query.isEmpty()) || statusFilter != null || searchBy != null) {
            page = loanService.searchLoans(query, pageable, Optional.empty(), IdType.NONE, statusFilter, searchBy, sort ,order);
        } else {
            // Otherwise, retrieve paginated members
            page = loanService.findPaginated(pageNo, pageSize, Optional.empty(), IdType.NONE);
        }

        List<LoanDto> listLoans = page.getContent();
        fineService.calculateFinesForOverdueLoans();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("loans", listLoans);
        model.addAttribute("query", query);
        model.addAttribute("statusFilter", statusFilter);
        model.addAttribute("searchBy", searchBy);
        model.addAttribute("sort", sort);
        model.addAttribute("order", order);
        return "manageloans";
    }

    // Delete loans
    @DeleteMapping("/manageloans/{id}")
    public String deleteLoans(@PathVariable(value = "id") Long id) {
        loanService.deleteLoans(id);
        return "redirect:/adminportal/manageloans?success";
    }

    // Direct to update loans page based on Loan Id
    @GetMapping("/updateloans")
    public String showUpdateLoansForm(@RequestParam(name = "loanId") Long id,
                                      Model model) {
        Optional<LoanDto> loanOptional = loanService.findById(id);

        if (loanOptional.isPresent()) {
            LoanDto loan = loanOptional.get();

            List<BookDto> allBooks = bookService.findAllBooks();
            List<MemberDto> allMembers = memberService.findAllUsers();

            model.addAttribute("loan", loan);
            model.addAttribute("allBooks", allBooks);
            model.addAttribute("allMembers", allMembers);

        } else {
            return "manageloans";
        }
        return "updateloans";
    }

    // Update/Save the (new) changes to the loan
    @PutMapping("/updateloans/{id}/save")
    public String updateLoans(@ModelAttribute("loan") LoanDto loan,
                              @PathVariable(value = "id") Long id,
                              @RequestParam(name = "memberId") Long memberId,
                              @RequestParam(name = "bookId") Long bookId) {
        loanService.updateLoans(loan, id, memberId, bookId);
        return "redirect:/adminportal/updateloans?loanId={id}&success";
    }

    // Retrieve the loan history based on Member Id.
    // If search filter is detected, searchLoans is used
    @GetMapping("/manageaccounts/loanhistory")
    public String showAccountLoanHistory(@RequestParam(name = "memberId") Long id,
                                @RequestParam(name = "page", defaultValue = "1") int pageNo,
                              @RequestParam(name = "query", required = false) String query,
                             @RequestParam(name = "searchBy", required = false) String searchBy,
                             @RequestParam(name = "statusFilter", required = false) String statusFilter,
                             @RequestParam(name = "sort", required = false) String sort,
                             @RequestParam(name = "order", required = false) String order,
                              Model model) {
        int pageSize = 5;
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<LoanDto> page;
        if ((query != null && !query.isEmpty()) || statusFilter != null){
            page = loanService.searchLoans(query, pageable, Optional.of(id), IdType.MEMBER_ID,
                    statusFilter, searchBy, sort, order);
        } else {
            // Otherwise, retrieve paginated members
            page = loanService.findPaginated(pageNo, pageSize, Optional.of(id), IdType.MEMBER_ID);
        }

        List<LoanDto> listLoans = page.getContent();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("loans", listLoans);
        model.addAttribute("query", query);
        model.addAttribute("statusFilter", statusFilter);
        model.addAttribute("member_id", id);
        model.addAttribute("searchBy", searchBy);
        model.addAttribute("sort", sort);
        model.addAttribute("order", order);
        return "accountloanhistory";
    }

    // Retrieve the loan history based on Book Id
    // If search filter is detected, searchLoans is used
    @GetMapping("/managebooks/loanhistory")
    public String showBookLoanHistory(@RequestParam(name = "bookId") Long id,
                                         @RequestParam(name = "page", defaultValue = "1") int pageNo,
                                         @RequestParam(name = "query", required = false) String query,
                                      @RequestParam(name = "searchBy", required = false) String searchBy,
                                      @RequestParam(name = "statusFilter", required = false) String statusFilter,
                                      @RequestParam(name = "sort", required = false) String sort,
                                      @RequestParam(name = "order", required = false) String order,
                                      Model model) {
        int pageSize = 5;

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<LoanDto> page;
        if ((query != null && !query.isEmpty()) || statusFilter != null || searchBy != null){
            page = loanService.searchLoans(query, pageable, Optional.of(id), IdType.BOOK_ID, statusFilter, searchBy, sort, order);
        } else {
            // Otherwise, retrieve paginated members
            page = loanService.findPaginated(pageNo, pageSize, Optional.of(id), IdType.BOOK_ID);
        }

        List<LoanDto> listLoans = page.getContent();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("loans", listLoans);
        model.addAttribute("query", query);
        model.addAttribute("statusFilter", statusFilter);
        model.addAttribute("searchBy", searchBy);
        model.addAttribute("book_id", id);
        model.addAttribute("sort", sort);
        model.addAttribute("order", order);
        return "bookloanhistory";
    }



    /*--------------FINES----------------*/

    // Retrieve the pages for fines based on Fine Id
    // If search filter is detected, searchFines is used
    @GetMapping("/managefines")
    public String manageFines(@RequestParam(name = "page", defaultValue = "1") int pageNo,
                              @RequestParam(name = "query", required = false) String query,
                              @RequestParam(name = "searchBy", required = false) String searchBy,
                              @RequestParam(name = "statusFilter", required = false) String statusFilter,
                              @RequestParam(name = "sort", required = false) String sort,
                              @RequestParam(name = "order", required = false) String order,
                              Model model) {
        int pageSize = 5;
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<FineDto> page;
        if (query != null && !query.isEmpty() || statusFilter != null || searchBy != null) {
            page = fineService.searchFines(query, pageable, Optional.empty(), IdType.NONE, statusFilter, searchBy, sort, order);
        } else {
            // Otherwise, retrieve paginated members
            page = fineService.findPaginated(pageNo, pageSize, Optional.empty(), IdType.NONE);
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
        return "manageFines";
    }

    // Retrieve the update fines page
    @GetMapping("/updatefines")
    public String showUpdateFinesForm(@RequestParam(name = "fineId") Long id,
                                      Model model) {
        Optional<FineDto> fineOptional =fineService.findById(id);

        if (fineOptional.isPresent()) {
            FineDto fine = fineOptional.get();

            List<LoanDto> allLoans = loanService.findAllLoans();

            model.addAttribute("fine", fine);
            model.addAttribute("allLoans", allLoans);

        } else {
            return "managefines";
        }
        return "updatefines";
    }

    // Update/Save tphe changes made to any fine
    @PutMapping("/updatefines/{id}/save")
    public String updateLoans(@ModelAttribute("fine") FineDto fine,
                              @PathVariable(value = "id") Long id) {
        fineService.updateFines(fine, id);
        return "redirect:/adminportal/updatefines?fineId={id}&success";
    }

    // Delete fine
    @DeleteMapping("/managefines/{id}")
    public String deleteFines(@PathVariable(value = "id") Long id) {
        fineService.deleteFines(id);
        return "redirect:/adminportal/managefines?success";
    }

    // Retrieve the payment logs based on payment Id
    // If search filter is detected, searchPayments is used
    @GetMapping("/paymentlogs")
    public String showPaymentLogs(@RequestParam(name = "page", defaultValue = "1") int pageNo,
                                     @RequestParam(name = "query", required = false) String query,
                                  @RequestParam(name = "searchBy", required = false) String searchBy,
                                  @RequestParam(name = "statusFilter", required = false) String statusFilter,
                                  @RequestParam(name = "sort", required = false) String sort,
                                  @RequestParam(name = "order", required = false) String order,
                                  Model model) {
        int pageSize = 5;
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<PaymentDto> page;
        if (query != null && !query.isEmpty() || statusFilter != null || searchBy != null) {
            page = paymentService.searchPayments(query, pageable, Optional.empty(), IdType.NONE, statusFilter, searchBy, sort, order);
        } else {
            // Otherwise, retrieve paginated members
            page = paymentService.findPaginated(pageNo, pageSize, Optional.empty(), IdType.NONE);
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
        return "paymentLogs";
    }

    // Retrieve Account Settings page
    @GetMapping("/accountsettings")
    public String showAccountSettings(Principal principal, Model model){
        String username = principal.getName();
        MemberDto member = memberService.findByEmail(username);
        model.addAttribute("member", member);
        return "admin/accountSettings/accountSettings";
    }

    // Retrieve the update name page
    @GetMapping("/accountsettings/updatename")
    public String showUpdateName (Principal principal, Model model){
        String username = principal.getName();
        MemberDto member = memberService.findByEmail(username);
        model.addAttribute("member", member);
        return "admin/accountSettings/updateName";
    }

    // Update/Save any changes made to name
    @PutMapping("/accountsettings/updatename/save")
    public String updateName (@ModelAttribute("member") MemberDto memberDto, Principal principal){
        String username = principal.getName();
        memberService.updateName(memberDto, username);
        return "redirect:/adminportal/accountsettings?success";
    }

    // Retrieve the update email page
    @GetMapping("/accountsettings/updateemail")
    public String showUpdateEmail (Principal principal, Model model){
        String username = principal.getName();
        MemberDto member = memberService.findByEmail(username);
        model.addAttribute("member", member);
        return "admin/accountSettings/updateEmail";
    }

    // Update/Save any changes to email
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
        return "admin/accountSettings/updateGender";
    }

    // Update/Save any changes to gender
    @PutMapping("/accountsettings/updategender/save")
    public String updateGender (@ModelAttribute("member") MemberDto memberDto, Principal principal){
        String username = principal.getName();
        memberService.updateGender(memberDto, username);
        return "redirect:/adminportal/accountsettings?success";
    }

    // Retrieve the update address one page
    @GetMapping("/accountsettings/updateaddressone")
    public String showUpdateAddressOne (Principal principal, Model model){
        String username = principal.getName();
        MemberDto member = memberService.findByEmail(username);
        model.addAttribute("member", member);
        return "admin/accountSettings/updateAddressOne";
    }

    // Update/Save any changes made to address one
    @PutMapping("/accountsettings/updateaddressone/save")
    public String updateAddressOne (@ModelAttribute("member") MemberDto memberDto, Principal principal){
        String username = principal.getName();
        memberService.updateAddressOne(memberDto, username);
        return "redirect:/adminportal/accountsettings?success";
    }

    // Retrieve the update address two page
    @GetMapping("/accountsettings/updateaddresstwo")
    public String showUpdateAddressTwo (Principal principal, Model model){
        String username = principal.getName();
        MemberDto member = memberService.findByEmail(username);
        model.addAttribute("member", member);
        return "admin/accountSettings/updateAddressTwo";
    }

    // Update/Save any changes made to address two
    @PutMapping("/accountsettings/updateaddresstwo/save")
    public String updateAddressTwo (@ModelAttribute("member") MemberDto memberDto, Principal principal){
        String username = principal.getName();
        memberService.updateAddressTwo(memberDto, username);
        return "redirect:/adminportal/accountsettings?success";
    }

    // Retrieve the update postal code page
    @GetMapping("/accountsettings/updatepostalcode")
    public String showUpdatePostalCode (Principal principal, Model model){
        String username = principal.getName();
        MemberDto member = memberService.findByEmail(username);
        model.addAttribute("member", member);
        return "admin/accountSettings/updatePostalCode";
    }

    // Update/Save any changes made to postal code
    @PutMapping("/accountsettings/updatepostalcode/save")
    public String updatePostalCode (@ModelAttribute("member") MemberDto memberDto, Principal principal){
        String username = principal.getName();
        memberService.updatePostalCode(memberDto, username);
        return "redirect:/adminportal/accountsettings?success";
    }

    // Retrieve the update password page
    @GetMapping("/accountsettings/updatepassword")
    public String showUpdatePassword (Principal principal, Model model){
        String username = principal.getName();
        MemberDto member = memberService.findByEmail(username);
        model.addAttribute("member", member);
        return "admin/accountSettings/updatePassword";
    }

    // Update/Save changes made to password
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
            return "admin/accountSettings/updatePassword";
        }

        memberService.updatePassword(newPassword, username);
        return "redirect:/adminportal/accountsettings?success";
    }
}

