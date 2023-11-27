package com.app.lms.repository;

import com.app.lms.entity.Author;
import com.app.lms.entity.Book;
import com.app.lms.entity.BookStatus;
import com.app.lms.entity.Member;
import com.app.lms.service.BookService;
import com.app.lms.web.BookDto;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;

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
        book.setStatus(BookStatus.AVAILABLE);

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
    //@Param("query") String query, BookStatus statusFilter, Pageable pageable
    @Test
    void itShouldCheckIfBookExistsIfSearchForBooksWithStatusAndByAny(){

        Book book = new Book();
        book.setTitle("Magical creatures and their majestic ways");
        book.setCategory("Thriller");
        book.setDescription("Set in the 19th century, Harry and his friend");
        book.setPublication_year("1994");
        book.setImage("img.jpg");
        book.setStatus(BookStatus.AVAILABLE);

        Author author = new Author();
        author.setFirstName("Harry");
        author.setLastName("Potter");
        Collection<Author> authorList = new ArrayList<>();
        authorList.add(author);
        book.setAuthors(authorList);
        bookRepository.save(book);

        int pageSize = 5;

        Pageable pageable = PageRequest.of(0, pageSize);
        String query = "creatures";
        BookStatus statusFilter = BookStatus.AVAILABLE;

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "title"); // Default sorting by title

        Page<Book> bookList =  bookRepository.searchForBooksWithStatusAndByAny(query, statusFilter,
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));

        boolean exists = false;
        if (bookList.hasContent()) exists = true;

        assertThat(exists).isTrue();
    }

    @Test
    void itShouldCheckIfBookDoesNotExistIfSearchForBooksWithStatusAndByAny(){
        int pageSize = 5;

        Pageable pageable = PageRequest.of(0, pageSize);
        String query = "Hello";
        BookStatus statusFilter = BookStatus.AVAILABLE;

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "title"); // Default sorting by title

        Page<Book> bookList =  bookRepository.searchForBooksWithStatusAndByAny(query, statusFilter,
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));

        boolean exists = true;
        if (!bookList.hasContent()) exists = false;

        assertThat(exists).isFalse();
    }

    @Test
    void itShouldCheckIfBookExistsIfSearchForBooksWithStatusAndNotAny(){
        Book book = new Book();
        book.setTitle("Magical creatures and their majestic ways");
        book.setCategory("Thriller");
        book.setDescription("Set in the 19th century, Harry and his friend");
        book.setPublication_year("1994");
        book.setImage("img.jpg");
        book.setStatus(BookStatus.AVAILABLE);

        Author author = new Author();
        author.setFirstName("Harry");
        author.setLastName("Potter");
        Collection<Author> authorList = new ArrayList<>();
        authorList.add(author);
        book.setAuthors(authorList);
        bookRepository.save(book);

        int pageSize = 5;

        Pageable pageable = PageRequest.of(0, pageSize);
        String query = "creatures";
        String searchBy = "title";
        BookStatus statusFilter = BookStatus.AVAILABLE;

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "title"); // Default sorting by title

        Page<Book> bookList =  bookRepository.searchForBooksWithStatusAndNotAny(query, searchBy, statusFilter,
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));

        boolean exists = false;
        if (bookList.hasContent()) exists = true;

        assertThat(exists).isTrue();
    }

    @Test
    void itShouldCheckIfBookDoesNotExistIfSearchForBooksWithStatusAndNotAny(){
        int pageSize = 5;

        Pageable pageable = PageRequest.of(0, pageSize);
        String query = "Hello";
        String searchBy = "title";
        BookStatus statusFilter = BookStatus.AVAILABLE;

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "title"); // Default sorting by title

        Page<Book> bookList =  bookRepository.searchForBooksWithStatusAndNotAny(query, searchBy, statusFilter,
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));

        boolean exists = true;
        if (!bookList.hasContent()) exists = false;

        assertThat(exists).isFalse();
    }
}