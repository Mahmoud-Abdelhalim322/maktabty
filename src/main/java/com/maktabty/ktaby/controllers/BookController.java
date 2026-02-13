package com.maktabty.ktaby.controllers;

import com.maktabty.ktaby.entities.Book;
import com.maktabty.ktaby.entities.User;
import com.maktabty.ktaby.repositories.BookRepository;
import com.maktabty.ktaby.repositories.BookTypeRepository;
import com.maktabty.ktaby.repositories.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookRepository bookRepository;
    private final BookTypeRepository bookTypeRepository;
    private final UserRepository userRepository;

    public BookController(BookRepository bookRepository, BookTypeRepository bookTypeRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.bookTypeRepository = bookTypeRepository;
        this.userRepository = userRepository;
    }
    
    @GetMapping
    public String list(Model model, Authentication authentication) {
        List<Book> books = bookRepository.findAll();
        model.addAttribute("books", books);
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username);
            model.addAttribute("favorites", user.getFavorites());
        }
        return "books/list";
    }
    
    @GetMapping("/add")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public String add(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("bookTypes", bookTypeRepository.findAll());
        return "books/add";
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @SuppressWarnings("null")
    public String save(@ModelAttribute("book") Book book, BindingResult result, Model model,
                       @RequestParam(value = "title", required = true) String title,
                       @RequestParam(value = "author", required = true) String author,
                       @RequestParam(value = "description", required = false) String description,
                       @RequestParam(value = "bookType", required = true) Long bookTypeId,
                       @RequestParam(value = "pdfFile", required = true) MultipartFile pdfFile,
                       RedirectAttributes redirectAttributes) {
        try {
            if (title == null || title.trim().isEmpty()) {
                model.addAttribute("error", "please enter book title!");
                model.addAttribute("bookTypes", bookTypeRepository.findAll());
                return "books/add";
            }
            
            if (author == null || author.trim().isEmpty()) {
                model.addAttribute("error", "please enter book author!");
                model.addAttribute("bookTypes", bookTypeRepository.findAll());
                return "books/add";
            }
            
            
    
            var bookType = bookTypeRepository.findById(bookTypeId)
                    .orElse(null);
            if (bookType == null) {
                model.addAttribute("error", "please select a valid book type!");
                model.addAttribute("bookTypes", bookTypeRepository.findAll());
                return "books/add";
            }
            
    
            Book newBook = new Book();
            newBook.setTitle(title.trim());
            newBook.setAuthor(author.trim());
            newBook.setDescription(description != null ? description.trim() : "");
            newBook.setBookType(bookType);


            if (pdfFile == null || pdfFile.isEmpty()) {
                model.addAttribute("error", "Please upload a PDF file!");
                model.addAttribute("bookTypes", bookTypeRepository.findAll());
                return "books/add";
            }
       
            if (pdfFile != null && !pdfFile.isEmpty()) {
                String contentType = pdfFile.getContentType();
                if (contentType == null || !contentType.contains("pdf")) {
                    model.addAttribute("error", "enter a pdf file only" + contentType + ")");
                    model.addAttribute("bookTypes", bookTypeRepository.findAll());
                    return "books/add";
                }
                
                long fileSizeInMB = pdfFile.getSize() / (1024 * 1024);
                if (pdfFile.getSize() > 50 * 1024 * 1024) {
                    model.addAttribute("error", "sorry size of file is(" + fileSizeInMB + "max 50MB)");
                    model.addAttribute("bookTypes", bookTypeRepository.findAll());
                    return "books/add";
                }
                
                String originalFilename = pdfFile.getOriginalFilename();
                if (originalFilename != null && !originalFilename.isEmpty()) {
                    newBook.setPdfFileName(originalFilename);
                    newBook.setPdfFile(pdfFile.getBytes());
                }
            }
            
      
            bookRepository.save(newBook);
            redirectAttributes.addFlashAttribute("success", "saved successfully ");
            return "redirect:/books";
            
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "error while saving:" + e.getMessage());
            model.addAttribute("bookTypes", bookTypeRepository.findAll());
            return "books/add";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "error while saving:" + e.getMessage());
            model.addAttribute("bookTypes", bookTypeRepository.findAll());
            return "books/add";
        }
    }
    
    @GetMapping("/{id}")
    @SuppressWarnings("null")
    public String view(@PathVariable("id") Long id, Model model, Authentication authentication) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("can't find book with id: " + id));
        model.addAttribute("book", book);
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username);
            model.addAttribute("isFavorite", user.getFavorites().contains(book));
        } else {
            model.addAttribute("isFavorite", false);
        }
        return "books/view";
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @SuppressWarnings("null")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            // Remove the book from all users' favorites before deleting
            userRepository.removeBookFromAllFavorites(id);
            bookRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "book");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "failed   deletion " + e.getMessage());
        }
        return "redirect:/books";
    }
    
    @GetMapping("/{id}/view-pdf")
    @SuppressWarnings("null")
    public ResponseEntity<byte[]> viewPdf(@PathVariable("id") Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("can't find book with id: " + id));
        
        if (book.getPdfFile() == null || book.getPdfFile().length == 0) {
            throw new IllegalArgumentException("no PDF file found for this book");
        }
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + book.getPdfFileName() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(book.getPdfFile());
    }
    
    @GetMapping("/{id}/download")
    @SuppressWarnings("null")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable("id") Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("can't find book with id: " + id));
        
        if (book.getPdfFile() == null || book.getPdfFile().length == 0) {
            throw new IllegalArgumentException("no PDF file found for this book");
        }
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + book.getPdfFileName() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(book.getPdfFile());
    }
}