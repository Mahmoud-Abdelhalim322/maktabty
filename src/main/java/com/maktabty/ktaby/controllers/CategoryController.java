package com.maktabty.ktaby.controllers;

import com.maktabty.ktaby.entities.Category;
import com.maktabty.ktaby.repositories.CategoryRepository;
import com.maktabty.ktaby.repositories.BookTypeRepository;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final BookTypeRepository bookTypeRepository;

    public CategoryController(CategoryRepository categoryRepository, BookTypeRepository bookTypeRepository) {
        this.categoryRepository = categoryRepository;
        this.bookTypeRepository = bookTypeRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        return "categories/list";
    }
    
    @GetMapping("/add")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public String add(Model model) {
        model.addAttribute("category", new Category());
        return "categories/add";
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @SuppressWarnings("null")
    public String save(@Valid @ModelAttribute("category") Category category, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "categories/add";
        }
        try {
            categoryRepository.save(category);
            redirectAttributes.addFlashAttribute("success", "saved successfully ");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "error " + e.getMessage());
        }
        return "redirect:/categories";
    }
    
    @GetMapping("/{id}")
    @SuppressWarnings("null")
    public String view(@PathVariable("id") Long id, Model model) {
        var category = categoryRepository.findById(id)
                         .orElseThrow(() -> new RuntimeException("can't find category with id: " + id));
        model.addAttribute("category", category);
        model.addAttribute("bookTypes", bookTypeRepository.findByCategory(category));
        return "categories/view";
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @SuppressWarnings("null")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "done deleted all successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", " failed to delete" + e.getMessage());
        }
        return "redirect:/categories";
    }
}
