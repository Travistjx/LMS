package com.app.lms.service.serviceimpl;

import com.app.lms.entity.Author;
import com.app.lms.entity.Book;
import com.app.lms.entity.BookStatus;
import com.app.lms.repository.AuthorRepository;
import com.app.lms.repository.BookRepository;
import com.app.lms.service.BookService;
import com.app.lms.web.AuthorDto;
import com.app.lms.web.BookDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    private BookService bookService;

    @BeforeEach
    void setup (){
        bookService = new BookServiceImpl(bookRepository, authorRepository);
    }


    @Test
    void canSaveBook() {
        BookDto book = new BookDto();
        book.setTitle("Harry Potter");
        byte[] fileContent = "test".getBytes();
        MultipartFile file = new MockMultipartFile("filename.txt", "original_filename.txt", "text/plain", fileContent);
        bookService.saveBook(book, file);

        ArgumentCaptor<Book> bookArgumentCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(bookArgumentCaptor.capture());
        Book capturedBook = bookArgumentCaptor.getValue();

        assertThat(capturedBook.getTitle()).isEqualTo(book.getTitle());
    }

    @Test
    void canFindByBook() {
        String title = "Harry";
        bookService.findByBook(title);
        verify(bookRepository).findByTitle(title);
    }

    @Test
    void canFindAllBooks() {
        bookService.findAllBooks();
        verify(bookRepository).findAll();
    }

    @Test
    void canFindPaginated() {
        int pageSize = 5;
        int pageNo = 1;

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        when(bookRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        bookService.findPaginated(pageNo, pageSize);

        verify(bookRepository).findAll(pageable);
    }

    @Test
    void canFindById() {
        long id = (long)1;
        bookService.findById(id);
        verify(bookRepository).findById(id);
    }

    @Test
    void canUpdateBooks() {
        Book book = new Book();
        book.setTitle("Harry");
        book.setBook_id(1L);
        book.setDescription("Hello there");
        book.setStatus(BookStatus.AVAILABLE);
        book.setPublication_year("1991");
        book.setCategory("Thriller");
        book.setImage("image.png");
        Author author = new Author();
        author.setFirstName("J.K.");
        author.setLastName("Rowling");
        Collection<Author> authorList = new ArrayList<>();
        authorList.add(author);
        book.setAuthors(authorList);

        when(bookRepository.findById(book.getBook_id())).thenReturn(Optional.of(book));

        BookDto bookDto = new BookDto();
        bookDto.setTitle("Alex");
        bookDto.setDescription("hello");
        bookDto.setCategory("thriller");
        bookDto.setPublication_year("1999");
        bookDto.setImage("test.jpg");
        bookDto.setStatus(BookStatus.AVAILABLE);


        AuthorDto author2 = new AuthorDto();
        author.setFirstName("test");
        author.setLastName("ing");
        Collection<AuthorDto> authorList2 = new ArrayList<>();
        authorList2.add(author2);
        bookDto.setAuthors(authorList2);

        byte[] fileContent = "test".getBytes();
        MultipartFile file = new MockMultipartFile("filename.txt", "original_filename.txt", "text/plain", fileContent);

        bookService.updateBooks(bookDto, (long)1, file, "Eoin");

        ArgumentCaptor <Book> bookArgumentCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(bookArgumentCaptor.capture());
        Book capturedBook = bookArgumentCaptor.getValue();

        assertThat(capturedBook.getTitle()).isEqualTo(bookDto.getTitle());
        assertThat(capturedBook.getDescription()).isEqualTo(bookDto.getDescription());
        assertThat(capturedBook.getCategory()).isEqualTo(bookDto.getCategory());
        assertThat(capturedBook.getStatus()).isEqualTo(bookDto.getStatus());
        assertThat(capturedBook.getPublication_year()).isEqualTo(bookDto.getPublication_year());
    }

    @Test
    void canDeleteBooks() {
        Book book = new Book();
        book.setTitle("Harry");
        book.setBook_id((long)1);
        book.setDescription("Hello there");
        book.setStatus(BookStatus.AVAILABLE);
        book.setPublication_year("1991");
        book.setCategory("Thriller");
        book.setImage("image.png");
        Author author = new Author();
        author.setFirstName("J.K.");
        author.setLastName("Rowling");
        Collection<Author> authorList = new ArrayList<>();
        authorList.add(author);
        book.setAuthors(authorList);

        when(bookRepository.findById(book.getBook_id())).thenReturn(Optional.of(book));

        bookService.deleteBooks((long)1);

        ArgumentCaptor <Book> bookArgumentCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(bookArgumentCaptor.capture());
        Book capturedBook = bookArgumentCaptor.getValue();

        assertThat(capturedBook.isDeleted()).isEqualTo(true);
    }

    @Test
    void canSearchBooks() {
        int pageSize = 5;
        int pageNo = 1;
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

        String query = "george";
        String searchBy = "title";
        BookStatus statusFilter = BookStatus.AVAILABLE;
        String sort = "title";
        String order = "asc";

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "title");

        when(bookRepository.searchForBooksWithStatusAndNotAny(query,  searchBy, statusFilter,PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));


        bookService.searchBooks(query, pageable, searchBy, "AVAILABLE", sort, order);

        verify(bookRepository).searchForBooksWithStatusAndNotAny(query, searchBy, statusFilter,PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));
    }

    @Test
    void canConvertEntityToDto() {
        Book book = new Book();
        book.setTitle("Harry");
        book.setBook_id((long)1);
        book.setDescription("Hello there");
        book.setStatus(BookStatus.AVAILABLE);
        book.setPublication_year("1991");
        book.setCategory("Thriller");
        book.setImage("image.png");
        Author author = new Author();
        author.setFirstName("J.K.");
        author.setLastName("Rowling");
        Collection<Author> authorList = new ArrayList<>();
        authorList.add(author);
        book.setAuthors(authorList);

        BookDto bookDto = bookService.convertEntityToDto(book);

        assertThat(book.getTitle()).isEqualTo(bookDto.getTitle());
        assertThat(book.getCategory()).isEqualTo(bookDto.getCategory());
        assertThat(book.getDescription()).isEqualTo(bookDto.getDescription());
        assertThat(book.getImage()).isEqualTo(bookDto.getImage());
        assertThat(book.getStatus()).isEqualTo(bookDto.getStatus());
        assertThat(book.getAuthors().stream().findFirst().get().getFirstName()).isEqualTo(bookDto.getAuthors().stream().findFirst().get().getFirstName());
        assertThat(book.getPublication_year()).isEqualTo(bookDto.getPublication_year());

    }
}