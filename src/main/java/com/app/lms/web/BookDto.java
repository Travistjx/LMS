package com.app.lms.web;

import com.app.lms.entity.BookStatus;
import com.app.lms.entity.LoanStatus;

import java.util.ArrayList;
import java.util.Collection;

public class BookDto {

    private Long book_id;
    private String title;
    private String description;
    private String category;


    private String publication_year;
    private Collection<AuthorDto> authors;
    private String image;

    private BookStatus status;

    public BookDto() {
        authors = new ArrayList<>();
        this.status = BookStatus.AVAILABLE;
    }



    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public Collection<AuthorDto> getAuthors() {
        return authors;
    }

    public void setAuthors(Collection<AuthorDto> authors) {
        this.authors = authors;
    }

    public void setPublication_year(String publication_year) {
        this.publication_year = publication_year;
    }

    public Long getBook_id() {
        return book_id;
    }

    public void setBook_id(Long book_id) {
        this.book_id = book_id;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }
}
