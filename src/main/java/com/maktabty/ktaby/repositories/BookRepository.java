package com.maktabty.ktaby.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.maktabty.ktaby.entities.Book;
import com.maktabty.ktaby.entities.BookType;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByBookType(BookType bookType);
    List<Book> findByTitleContainingIgnoreCase(String title);
    List<Book> findByAuthorContainingIgnoreCase(String author);
    List<Book> findByDescriptionContainingIgnoreCase(String description);
}
