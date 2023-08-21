package com.app.lms.repository;

import com.app.lms.entity.Author;
import com.app.lms.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    Author findByFirstNameAndLastName(String firstName, String lastName);
}
