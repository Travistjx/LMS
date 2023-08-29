package com.app.lms.service;

import com.app.lms.entity.Book;
import com.app.lms.web.BookDto;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface BookService {

    void saveBook(BookDto bookDto, MultipartFile file);

    Book findByBook(String title);

    List<BookDto> findAllBooks();

    Page<BookDto> findPaginated(int pageNo, int pageSize);

    Optional<BookDto> findById(long id);

    void updateBooks(BookDto book, Long id, MultipartFile file, String authors);
    void deleteBooks(Long id);

}
