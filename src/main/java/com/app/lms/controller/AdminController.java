package com.app.lms.controller;

import com.app.lms.entity.Book;
import com.app.lms.entity.Member;
import com.app.lms.service.AdminService;
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

@Controller
public class AdminController {

    private final MemberService memberService;
    private final AdminService adminService;

    public AdminController(MemberService memberService, AdminService adminService) {
        this.memberService = memberService;
        this.adminService = adminService;
    }

    @GetMapping("/adminportal")
    public String listRegisteredUsers(){
        return "adminportal";
    }

    @GetMapping("/adminportal/addbooks")
    public String showAddBooksForm(Model model){
        BookDto book = new BookDto();
        model.addAttribute("book", book);
        return "addbooks";
    }
    @GetMapping("/adminportal/createaccount")
    public String createAccount(Model model){
        MemberDto member = new MemberDto();
        model.addAttribute("member", member);
        return "createaccount";
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

    @GetMapping("/adminportal/books")
    public String showBooks(Model model){
//        List<BookDto> books = adminService.findAllBooks();
//        model.addAttribute("books", books);
//        return "books";
        return findPaginated(1, model);
    }

    @GetMapping("/adminportal/managebooks")
    public String manageBooks(Model model){
        List<BookDto> books = adminService.findAllBooks();
        model.addAttribute("books", books);
        return "managebooks";
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
        adminService.saveBook(book, file);
        return "redirect:/adminportal/addbooks?success";
    }

    @GetMapping("/adminportal/books/page/{pageNo}")
    public String findPaginated(@PathVariable (value = "pageNo") int pageNo, Model model){
        int pageSize = 3;
        Page<BookDto> page = adminService.findPaginated(pageNo, pageSize);
        List<BookDto> listBooks = page.getContent();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("books", listBooks);
        return "books";
    }
}
