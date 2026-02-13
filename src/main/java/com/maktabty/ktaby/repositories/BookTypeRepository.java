package com.maktabty.ktaby.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.maktabty.ktaby.entities.BookType;
import com.maktabty.ktaby.entities.Category;
import java.util.List;

public interface BookTypeRepository extends JpaRepository<BookType, Long> {
    List<BookType> findByCategory(Category category);
    List<BookType> findByTypeNameContainingIgnoreCase(String typeName);
}
