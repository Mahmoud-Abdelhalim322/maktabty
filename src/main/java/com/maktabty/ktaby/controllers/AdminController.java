package com.maktabty.ktaby.controllers;

import com.maktabty.ktaby.entities.Role;
import com.maktabty.ktaby.entities.User;
import com.maktabty.ktaby.repositories.RoleRepository;
import com.maktabty.ktaby.repositories.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public AdminController(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @SuppressWarnings("null")
    @GetMapping("/users/{id}/roles")
    public String editUserRoles(@PathVariable("id") Long id, Model model) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        List<Role> allRoles = roleRepository.findAll();
        model.addAttribute("user", user);
        model.addAttribute("allRoles", allRoles);
        return "admin/edit-roles";
    }

    @SuppressWarnings("null")
    @PostMapping("/users/{id}/roles")
    public String updateUserRoles(@PathVariable("id") Long id,
                                  @RequestParam("roleIds") List<Long> roleIds,
                                  RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        Set<Role> roles = roleRepository.findAllById(roleIds).stream().collect(java.util.stream.Collectors.toSet());
        user.setRoles(roles);
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("success", "Roles updated successfully!");
        return "redirect:/admin/users";
    }
}