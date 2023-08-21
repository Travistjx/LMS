package com.app.lms.web;

import com.app.lms.entity.Author;
import com.app.lms.entity.Role;
import jakarta.persistence.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collection;

public class BookDto {
    private String title;
    private String description;
    private String category;
    private String publication_year;
    private Collection<AuthorDto> authors;
    private String image;

    public BookDto() {
        authors = new ArrayList<>();
    }

    public BookDto(String title, String description, String category, String publication_year, Collection<AuthorDto> authors,
                   String image) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.publication_year = publication_year;
        this.authors = authors;
        this.image = image;
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
}
