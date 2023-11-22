package com.app.lms.controller;

import com.app.lms.entity.Member;
import com.app.lms.service.MemberService;
import com.app.lms.web.BookDto;
import com.app.lms.web.MemberDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
public class AuthController {

    private final MemberService memberService;

    public AuthController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/")
    public String index(){
        return "index";
    }

    @RequestMapping("/success")
    public void loginPageRedirect(HttpServletRequest request, HttpServletResponse response, Authentication authResult) throws IOException {

        String role =  authResult.getAuthorities().toString();

        if(role.contains("ROLE_ADMIN")){
            response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/adminportal"));
        }
        else if(role.contains("ROLE_MEMBER")) {
            response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/home"));
        }
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }




    // handler method to handle user registration request
    @GetMapping("/register")
    public String showRegistrationForm(Model model){
        MemberDto member = new MemberDto();
        model.addAttribute("member", member);
        return "register";
    }

    // handler method to handle register user form submit request
    @PostMapping("/register/save")
    public String registration( @ModelAttribute("member") MemberDto member,
                               BindingResult result,
                               Model model){
        MemberDto existing = memberService.findByEmail(member.getEmail());
        if (existing != null) {
            result.rejectValue("email", null, "There is already an account registered with that email");
        }
        if (result.hasErrors()) {
            model.addAttribute("member", member);
            return "register";
        }
        memberService.saveUser(member);
        return "redirect:/register?success";
    }


}