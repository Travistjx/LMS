package com.app.lms.controller;

import com.app.lms.entity.Member;
import com.app.lms.service.BookService;
import com.app.lms.service.MemberService;
import com.app.lms.web.BookDto;
import com.app.lms.web.MemberDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    private final MemberService memberService;
    private final BookService bookService;

    public AdminController(MemberService memberService, BookService bookService) {
        this.memberService = memberService;
        this.bookService = bookService;
    }

    @GetMapping("/adminportal")
    public String homePage(){
        return "adminportal";
    }

    @GetMapping("/adminportal/addbooks")
    public String showAddBooksForm(Model model){
        BookDto book = new BookDto();
        model.addAttribute("book", book);
        return "addbooks";
    }

    @GetMapping("/adminportal/managebooks")
    public String manageBooks(Model model){
//        List<BookDto> books = adminService.findAllBooks();
//        model.addAttribute("books", books);

        return findPaginatedManageBooks(1, model);
    }
    @GetMapping("/adminportal/createaccount")
    public String createAccount(Model model){
        MemberDto member = new MemberDto();
        model.addAttribute("member", member);
        return "createaccount";
    }

    @GetMapping("/adminportal/manageaccounts")
    public String manageAccount(Model model){
        List<MemberDto> members = memberService.findAllUsers();
        model.addAttribute("members", members);
        return "manageaccounts";
    }

    @GetMapping("/adminportal/books")
    public String showBooks(Model model){
//        List<BookDto> books = adminService.findAllBooks();
//        model.addAttribute("books", books);
//        return "books";
        return findPaginatedAllBooks(1, model);
    }

    // handler method to handle create user form submit request
    @PostMapping("/adminportal/createaccount/save")
    public String registration( @ModelAttribute("member") MemberDto member,
                                BindingResult result,
                                Model model){
        Member existing = memberService.findByEmail(member.getEmail());
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

    @GetMapping("/adminportal/updateaccounts/{id}")
    public String showUpdateAccountForm(@PathVariable (value = "id") Long id, Model model){
        Optional<MemberDto> memberOptional = memberService.findById(id);
        if (memberOptional.isPresent()) {
            MemberDto member = memberOptional.get();
            model.addAttribute("member", member);

            String roles = member.getRoles().stream()
                    .map(role -> role.getName())
                    .collect(Collectors.joining(", "));
            model.addAttribute("roles", roles);
        }
        else {
            return "manageaccounts";
        }
        return "updateaccounts";
    }

    @PutMapping("/adminportal/updateaccounts/{id}/save")
    public String updateAccounts(@ModelAttribute("member") MemberDto member,
                              @RequestParam("currentRoles") String roles,
                              @PathVariable (value = "id") Long id){
        memberService.updateAccounts(member, id, roles);
        return "redirect:/adminportal/updateaccounts/{id}?success";
    }

    @GetMapping("/adminportal/updatebooks/{id}")
    public String showUpdateBookForm(@PathVariable (value = "id") Long id, Model model){
        Optional<BookDto> bookOptional = bookService.findById(id);
        if (bookOptional.isPresent()) {
            BookDto book = bookOptional.get();
            model.addAttribute("book", book);

            String authors = book.getAuthors().stream()
                    .map(author -> author.getFirstName() + ' ' + author.getLastName())
                    .collect(Collectors.joining(", "));
            model.addAttribute("authors", authors);
        }
        else {
            return "managebooks";
        }
        return "updatebooks";
    }

    @PutMapping("/adminportal/updatebooks/{id}/save")
    public String updateBooks(@ModelAttribute("book") BookDto book,
                              @RequestParam("imageFile") MultipartFile file,
                              @RequestParam("currentAuthors") String authors,
                              @PathVariable (value = "id") Long id){
        bookService.updateBooks(book, id, file, authors);
        return "redirect:/adminportal/updatebooks/{id}?success";
    }

    @DeleteMapping("/adminportal/managebooks/{id}")
    public String deleteBooks(@PathVariable (value = "id") Long id){
        bookService.deleteBooks(id);
        return "redirect:/adminportal/managebooks?success";
    }

    @PostMapping("/adminportal/addbooks/save")
    public String saveBooks( @ModelAttribute("book") BookDto book,
                             @RequestParam("imageFile") MultipartFile file,
                                BindingResult result,
                                Model model){
//        Book existing = adminService.findByBook(book.getTitle());
//        if (existing != null) {
//            result.rejectValue("title", null, "There is already an existing book");
//        }
//        if (result.hasErrors()) {
//            model.addAttribute("book", book);
//            return "addbooks";
//        }
        bookService.saveBook(book, file);
        return "redirect:/adminportal/addbooks?success";
    }

    @GetMapping("/adminportal/books/page/{pageNo}")
    public String findPaginatedAllBooks(@PathVariable (value = "pageNo") int pageNo, Model model){
        int pageSize = 3;
        Page<BookDto> page = bookService.findPaginated(pageNo, pageSize);
        List<BookDto> listBooks = page.getContent();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("books", listBooks);
        return "books";
    }

    @GetMapping("/adminportal/managebooks/page/{pageNo}")
    public String findPaginatedManageBooks(@PathVariable (value = "pageNo") int pageNo, Model model){
        int pageSize = 3;
        Page<BookDto> page = bookService.findPaginated(pageNo, pageSize);
        List<BookDto> listBooks = page.getContent();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("books", listBooks);
        return "managebooks";
    }
}
