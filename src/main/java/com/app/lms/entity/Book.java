package com.app.lms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
    private boolean deleted;

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
}
