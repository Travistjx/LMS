package com.app.lms.service;

import com.app.lms.entity.Book;
import com.app.lms.entity.User;
import com.app.lms.web.AuthorDto;
import com.app.lms.web.BookDto;
import com.app.lms.web.UserDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AdminService {

    void saveBook(BookDto bookDto, MultipartFile file);

    Book findByBook(String title);

    List<BookDto> findAllBooks();
}