package com.app.lms.web;

import com.app.lms.entity.BookStatus;
import com.app.lms.entity.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
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
}
