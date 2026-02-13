package com.maktabty.ktaby.controllers;

import com.maktabty.ktaby.entities.BookType;
import com.maktabty.ktaby.repositories.BookTypeRepository;
import com.maktabty.ktaby.repositories.CategoryRepository;
import com.maktabty.ktaby.repositories.BookRepository;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Controller
@RequestMapping("/booktypes")
public class BookTypeController {

    private final BookTypeRepository bookTypeRepository;
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;

    public BookTypeController(BookTypeRepository bookTypeRepository, CategoryRepository categoryRepository, BookRepository bookRepository) {
        this.bookTypeRepository = bookTypeRepository;
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
    }


    @GetMapping
    public String list(Model model) {
        List<BookType> bookTypes = bookTypeRepository.findAll();
        Map<String, List<BookType>> groupedBookTypes = new HashMap<>();
        for (BookType bt : bookTypes) {
            String catName = bt.getCategory() != null ? bt.getCategory().getName() : "N/A";
            groupedBookTypes.computeIfAbsent(catName, k -> new ArrayList<>()).add(bt);
        }
        model.addAttribute("groupedBookTypes", groupedBookTypes);
        return "booktypes/list";
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public String add(Model model) {
        model.addAttribute("bookType", new BookType());
        model.addAttribute("categories", categoryRepository.findAll());
        return "booktypes/add";
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @SuppressWarnings("null")
    public String save(@Valid @ModelAttribute("bookType") BookType bookType, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            return "booktypes/add";
        }
        try {
            bookTypeRepository.save(bookType);
            redirectAttributes.addFlashAttribute("success", "saved successfully ");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "error " + e.getMessage());
        }
        return "redirect:/booktypes";
    }

    @GetMapping("/{id}")
    @SuppressWarnings("null")
    public String view(@PathVariable("id") Long id, Model model) {
        BookType bookType = bookTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("can't find book type with id: " + id));
        model.addAttribute("bookType", bookType);
        model.addAttribute("books", bookRepository.findByBookType(bookType));
        return "booktypes/view";
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @SuppressWarnings("null")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            bookTypeRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "deleted all successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", " failed to delete" + e.getMessage());
        }
        return "redirect:/booktypes";
    }
}
