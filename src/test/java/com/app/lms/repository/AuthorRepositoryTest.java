package com.app.lms.repository;

import com.app.lms.entity.Author;
import com.app.lms.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    void itShouldCheckIfAuthorExistsByFirstNameAndLastName() {
        //given
        Author author = new Author();
        author.setFirstName("Daniel");
        author.setLastName("Lee");
        authorRepository.save(author);

        //when
        boolean exists = false;
        Author newAuthor = authorRepository.findByFirstNameAndLastName("Daniel", "Lee");
        if (newAuthor != null) exists = true;

        //then
        assertThat(exists).isTrue();
    }

    @Test
    void itShouldCheckIfAuthorDoesNotExistsByFirstNameAndLastName() {
        //given
        String firstName = "Daniel";
        String lastName = "Lee";

        //when
        boolean exists = true;
        Author newAuthor = authorRepository.findByFirstNameAndLastName(firstName, lastName);
        if (newAuthor == null) exists = false;

        //then
        assertThat(exists).isFalse();
    }
}