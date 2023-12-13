package com.app.lms.repository;

import com.app.lms.entity.Book;
import com.app.lms.entity.BookStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    Book findByTitle(String title);

    @Query("SELECT l FROM Book l WHERE l.deleted IS NULL OR l.deleted = false")
    List<Book> findAll();

    @Query("SELECT l FROM Book l WHERE l.deleted IS NULL OR l.deleted = false")
    Page<Book> findAll(Pageable pageable);

    @Query("SELECT b FROM Book b " +
            "LEFT JOIN b.authors a " +
            "WHERE b.deleted = false AND " +
            "(b.book_id LIKE %:query% OR " +
            "b.title LIKE %:query% OR " +
            "b.description LIKE %:query% OR " +
            "b.category LIKE %:query% OR " +
            "b.publication_year LIKE %:query% OR " +
            "b.status LIKE %:query% OR " +
            "a.firstName LIKE %:query% OR " +
            "a.lastName LIKE %:query%)" +
            "AND (:statusFilter IS NULL OR b.status = :statusFilter)")
    Page<Book> searchForBooksWithStatusAndByAny(@Param("query") String query, BookStatus statusFilter, Pageable pageable);


    @Query("Select b FROM Book b " +
            "LEFT JOIN b.authors a " +
            "WHERE b.deleted = false AND " +
            "((:searchBy = 'title' AND b.title LIKE %:query%) OR " +
            "((:searchBy = 'author' AND a.firstName LIKE %:query%) OR " +
            "(:searchBy = 'author' AND a.lastName LIKE %:query%)) OR " +
            "(:searchBy = 'category' AND b.category LIKE %:query%) OR " +
            "(:searchBy = 'publicationYear' AND b.publication_year LIKE %:query%) OR " +
            "(:searchBy = 'description' AND b.description LIKE %:query%)) " +
            "AND (:statusFilter IS NULL OR b.status = :statusFilter)")
    Page<Book> searchForBooksWithStatusAndNotAny(@Param("query") String query, @Param("searchBy") String searchBy,
                                                 BookStatus statusFilter, Pageable pageable);

}
