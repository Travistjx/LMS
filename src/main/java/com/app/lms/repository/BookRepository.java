package com.app.lms.repository;

import com.app.lms.entity.Book;
import com.app.lms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

    Book findByTitle(String title);
}
