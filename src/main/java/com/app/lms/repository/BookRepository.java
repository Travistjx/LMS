package com.app.lms.repository;

import com.app.lms.entity.Book;
import com.app.lms.entity.BookStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {

    Book findByTitle(String title);

    @Query("SELECT b FROM Book b " +
            "LEFT JOIN b.authors a " +
            "WHERE (b.book_id LIKE %:query% OR " +
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
            "WHERE " +
            "((:searchBy = 'title' AND b.title LIKE %:query%) OR " +
            "((:searchBy = 'author' AND a.firstName LIKE %:query%) OR " +
            "(:searchBy = 'author' AND a.lastName LIKE %:query%)) OR " +
            "(:searchBy = 'category' AND b.category LIKE %:query%) OR " +
            "(:searchBy = 'publicationYear' AND b.publication_year LIKE %:query%) OR " +
            "(:searchBy = 'description' AND b.description LIKE %:query%)) " +
            "AND (:statusFilter IS NULL OR b.status = :statusFilter)")
    Page<Book> searchForBooksWithStatusAndNotAny(@Param("query") String query, @Param("searchBy") String searchBy,
                                                 BookStatus statusFilter, Pageable pageable);

    @Query("SELECT b FROM Book b " +
            "WHERE(b.title LIKE %:query%)" +
            "AND (:statusFilter IS NULL OR b.status = :statusFilter)")
    Page<Book> searchForBooksByTitle(@Param("query") String query, Pageable pageable, BookStatus statusFilter);

    @Query("SELECT b FROM Book b " +
            "WHERE(b.description LIKE %:query%)" +
            "AND (:statusFilter IS NULL OR b.status = :statusFilter)")
    Page<Book> searchForBooksByDescription(@Param("query") String query, Pageable pageable, BookStatus statusFilter);

    @Query("SELECT b FROM Book b " +
            "LEFT JOIN b.authors a " +
            "WHERE (a.firstName LIKE %:query% OR " +
            "a.lastName LIKE %:query%)" +
            "AND (:statusFilter IS NULL OR b.status = :statusFilter)")
    Page<Book> searchForBooksByAuthor(@Param("query") String query, Pageable pageable, BookStatus statusFilter);

    @Query("SELECT b FROM Book b " +
            "WHERE (b.category LIKE %:query%)" +
            "AND (:statusFilter IS NULL OR b.status = :statusFilter)")
    Page<Book> searchForBooksByCategory(@Param("query") String query, Pageable pageable, BookStatus statusFilter);

    @Query("SELECT b FROM Book b " +
            "WHERE (b.publication_year LIKE %:query%)" +
            "AND (:statusFilter IS NULL OR b.status = :statusFilter)")
    Page<Book> searchForBooksByPublicationYear(@Param("query") String query, Pageable pageable, BookStatus statusFilter);


}
