package com.vakya.stock_application.controller;

import com.vakya.stock_application.model.User;
import com.vakya.stock_application.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model, @RequestParam(value = "error", required = false) String error) {
        model.addAttribute("user", new User());
        if (error != null) {
            model.addAttribute("errorMessage", error);
        }
        return "signup";
    }

    @PostMapping("/register")
    public Mono<String> registerUser(@ModelAttribute User user, Model model) {
        return userService.registerUser(user)
                .map(savedUser -> "redirect:/login?registered=true")
                .onErrorResume(ex -> {
                    model.addAttribute("errorMessage", ex.getMessage());
                    return Mono.just("signup");
                });
    }


    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
}
