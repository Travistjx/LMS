package com.app.lms.entity;

import jakarta.persistence.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
@Entity
@Table(name= "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long book_id;
    private String title;
    @Column(length = 2000)
    private String description;
    private String category;
    private String publication_year;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BookStatus status;


    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(
                    name = "book_id", referencedColumnName = "book_id"),
            inverseJoinColumns = @JoinColumn(
                    name = "author_id", referencedColumnName = "author_id"))
    private Collection<Author> authors;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }



    public Book() {
        this.status = BookStatus.AVAILABLE;
    }


    public Book(Long book_id, String title, String description, String category, String publication_year, Collection<Author> authors,
                String image) {
        this.book_id = book_id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.publication_year = publication_year;
        this.authors = authors;
        this.image = image;
    }

    public Long getBook_id() {
        return book_id;
    }

    public void setBook_id(Long book_id) {
        this.book_id = book_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPublication_year() {
        return publication_year;
    }

    public void setPublication_year(String publication_year) {
        this.publication_year = publication_year;
    }

    public Collection<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Collection<Author> author) {
        this.authors = author;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }
}
