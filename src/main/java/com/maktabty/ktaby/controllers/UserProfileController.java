package com.maktabty.ktaby.controllers;

import com.maktabty.ktaby.entities.User;
import com.maktabty.ktaby.entities.Book;
import com.maktabty.ktaby.repositories.UserRepository;
import com.maktabty.ktaby.repositories.BookRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class UserProfileController {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final PasswordEncoder passwordEncoder;

    public UserProfileController(UserRepository userRepository, BookRepository bookRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public String viewProfile(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        return "profile/view";
    }

    @GetMapping("/edit")
    @PreAuthorize("isAuthenticated()")
    public String editProfile(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        return "profile/edit";
    }

    @PostMapping("/update")
    @PreAuthorize("isAuthenticated()")
    public String updateProfile(@ModelAttribute("user") User updatedUser,
                                @RequestParam(value = "currentPassword", required = false) String currentPassword,
                                @RequestParam(value = "newPassword", required = false) String newPassword,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);

        if (newPassword != null && !newPassword.trim().isEmpty()) {
            if (currentPassword == null || !passwordEncoder.matches(currentPassword, user.getPassword())) {
                redirectAttributes.addFlashAttribute("error", "Current password is incorrect!");
                return "redirect:/profile/edit";
            }
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        userRepository.save(user);
        redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        return "redirect:/profile";
    }

    @GetMapping("/favorites")
    @PreAuthorize("isAuthenticated()")
    public String viewFavorites(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        model.addAttribute("favorites", user.getFavorites());
        return "profile/favorites";
    }

    @PostMapping("/favorites/add/{bookId}")
    @PreAuthorize("isAuthenticated()")
    public String addToFavorites(@PathVariable Long bookId, Authentication authentication, RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book != null && !user.getFavorites().contains(book)) {
            user.getFavorites().add(book);
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("success", "Book added to favorites!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Book not found or already in favorites!");
        }
        return "redirect:/books/" + bookId;
    }

    @PostMapping("/favorites/remove/{bookId}")
    @PreAuthorize("isAuthenticated()")
    public String removeFromFavorites(@PathVariable Long bookId, Authentication authentication, RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book != null && user.getFavorites().contains(book)) {
            user.getFavorites().remove(book);
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("success", "Book removed from favorites!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Book not found or not in favorites!");
        }
        return "redirect:/books/" + bookId;
    }
}