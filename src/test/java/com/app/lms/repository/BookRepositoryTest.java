package com.app.lms.repository;

import com.app.lms.entity.Author;
import com.app.lms.entity.Book;
import com.app.lms.entity.Member;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    void itShouldCheckIfBookExistsByTitle() {
        //given
        Book book = new Book();
        book.setTitle("Magical creatures and their majestic ways");
        book.setCategory("Thriller");
        book.setDescription("Set in the 19th century, Harry and his friend");
        book.setPublication_year("1994");
        book.setImage("img.jpg");

        Author author = new Author();
        author.setFirstName("Harry");
        author.setLastName("Potter");
        Collection<Author> authorList = new ArrayList<>();
        authorList.add(author);
        book.setAuthors(authorList);
        bookRepository.save(book);

        //when
        boolean exists = false;
        Book newBook = bookRepository.findByTitle("Magical creatures and their majestic ways");
        if (newBook != null) exists = true;

        //then
        assertThat(exists).isTrue();
    }

    @Test
    void itShouldCheckIfBookDoesNotExistsByTitle() {
        //given
        String title = "Magical creatures and their majestic ways";

        //when
        boolean exists = true;
        Book newBook = bookRepository.findByTitle(title);
        if (newBook == null) exists = false;

        //then
        assertThat(exists).isFalse();
    }
}