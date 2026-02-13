package com.maktabty.ktaby.services;

import com.maktabty.ktaby.entities.Book;
import com.maktabty.ktaby.entities.BookType;
import com.maktabty.ktaby.entities.Category;
import com.maktabty.ktaby.repositories.BookRepository;
import com.maktabty.ktaby.repositories.BookTypeRepository;
import com.maktabty.ktaby.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {

    private final BookRepository bookRepository;
    private final BookTypeRepository bookTypeRepository;
    private final CategoryRepository categoryRepository;

    public SearchService(BookRepository bookRepository, BookTypeRepository bookTypeRepository, CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.bookTypeRepository = bookTypeRepository;
        this.categoryRepository = categoryRepository;
    }

    public Map<String, List<?>> search(String query) {
        Map<String, List<?>> results = new HashMap<>();

        // Search books
        List<Book> books = new ArrayList<>();
        books.addAll(bookRepository.findByTitleContainingIgnoreCase(query));
        books.addAll(bookRepository.findByAuthorContainingIgnoreCase(query));
        books.addAll(bookRepository.findByDescriptionContainingIgnoreCase(query));
        results.put("books", books);

        // Search book types
        List<BookType> bookTypes = bookTypeRepository.findByTypeNameContainingIgnoreCase(query);
        results.put("bookTypes", bookTypes);

        // Search categories
        List<Category> categories = categoryRepository.findByNameContainingIgnoreCase(query);
        results.put("categories", categories);

        return results;
    }
}