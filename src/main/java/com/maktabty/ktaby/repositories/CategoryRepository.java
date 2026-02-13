package com.maktabty.ktaby.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.maktabty.ktaby.entities.Category;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByNameContainingIgnoreCase(String name);
}
