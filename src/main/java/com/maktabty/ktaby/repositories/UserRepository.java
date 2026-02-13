package com.maktabty.ktaby.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import com.maktabty.ktaby.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_favorites WHERE book_id = :bookId", nativeQuery = true)
    void removeBookFromAllFavorites(@Param("bookId") Long bookId);
}
