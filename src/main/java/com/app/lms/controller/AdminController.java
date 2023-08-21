package com.app.lms.controller;

import com.app.lms.entity.Book;
import com.app.lms.entity.User;
import com.app.lms.service.AdminService;
import com.app.lms.service.UserService;
import com.app.lms.web.AuthorDto;
import com.app.lms.web.BookDto;
import com.app.lms.web.UserDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class AdminController {

    private final UserService userService;
    private final AdminService adminService;

    public AdminController(UserService userService, AdminService adminService) {
        this.userService = userService;
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
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "createaccount";
    }

    // handler method to handle create user form submit request
    @PostMapping("/adminportal/createaccount/save")
    public String registration( @ModelAttribute("user") UserDto user,
                                BindingResult result,
                                Model model){
        User existing = userService.findByEmail(user.getEmail());
        if (existing != null) {
            result.rejectValue("email", null, "There is already an account registered with that email");
        }
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "createaccount";
        }
        userService.saveUser(user);
        return "redirect:/adminportal/createaccount?success";
    }

    @GetMapping("/adminportal/books")
    public String showBooks(Model model){
        List<BookDto> books = adminService.findAllBooks();
        model.addAttribute("books", books);
        return "books";
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
}
