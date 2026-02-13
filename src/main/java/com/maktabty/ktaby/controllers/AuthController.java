package com.maktabty.ktaby.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.maktabty.ktaby.entities.User;
import com.maktabty.ktaby.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class AuthController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            logger.warn("Login failed with error");
            model.addAttribute("error", "Invalid username or password");
        }
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(User user, Model model) {
        logger.info("Attempting to register user: {}", user.getUsername());
        try {
            User savedUser = userService.registerNewUser(user);
            logger.info("User registered successfully: {}", savedUser.getUsername());
            return "redirect:/login";
        } catch (Exception e) {
            logger.error("Error registering user: {}", e.getMessage(), e);
            model.addAttribute("error", e.getMessage());
            
            model.addAttribute("user", user);
            return "register";
        }
    }
}
