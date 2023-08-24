package com.app.lms.service.serviceimpl;

import com.app.lms.entity.Author;
import com.app.lms.entity.Book;
import com.app.lms.repository.AuthorRepository;
import com.app.lms.repository.BookRepository;
import com.app.lms.service.AdminService;
import com.app.lms.web.AuthorDto;
import com.app.lms.web.BookDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public AdminServiceImpl(BookRepository bookRepository,
                           AuthorRepository authorRepository
                          ) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;

    }

    @Override
    public void saveBook(BookDto bookDto, MultipartFile file) {
        Book book = new Book();
        book.setTitle(bookDto.getTitle());
        book.setDescription(bookDto.getDescription());
        book.setPublication_year(bookDto.getPublication_year()); // Use correct property name
        book.setCategory(bookDto.getCategory());

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        if(fileName.contains(".."))
        {
            System.out.println("not a a valid file");
        }
        try {
            book.setImage(Base64.getEncoder().encodeToString(file.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Initialize the authors collection
        Collection<Author> authors = new ArrayList<>();

        // Iterate through authorDto and add authors to the collection
        for (AuthorDto currentAuthorDto : bookDto.getAuthors()) {
            Author author = authorRepository.findByFirstNameAndLastName(
                    currentAuthorDto.getFirstName(), currentAuthorDto.getLastName());

            if (author == null) {
                Author newAuthor = new Author();
                newAuthor.setLastName(currentAuthorDto.getLastName());
                newAuthor.setFirstName(currentAuthorDto.getFirstName());
                authors.add(newAuthor);
            }
            else {
                authors.add(author);
            }

        }

        // Set the authors collection in the book
        book.setAuthors(authors);

        bookRepository.save(book);
    }

    public Author getAuthorByFullName(String firstName, String lastName) {
        return authorRepository.findByFirstNameAndLastName(firstName, lastName);
    }

    @Override
    public Book findByBook(String title) {
        return bookRepository.findByTitle(title);
    }

    @Override
    public List<BookDto> findAllBooks() {
        List<Book> books = bookRepository.findAll();
        return books.stream().map((book) -> convertEntityToDto(book))
                .collect(Collectors.toList());
    }

    @Override
    public Page<BookDto> findPaginated(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<Book> bookPage = this.bookRepository.findAll(pageable);

        List<BookDto> bookDtoList = bookPage.getContent().stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(bookDtoList, pageable, bookPage.getTotalElements());
    }

    @Override
    public Optional<BookDto> findById(long id) {
        Optional<Book> bookOptional = bookRepository.findById(id);
        return bookOptional.map(this::convertEntityToDto);
    }


    @Override
    public void updateBooks(BookDto book, Long bookId, MultipartFile file, String authors) {
        Optional<Book> selectedBook = bookRepository.findById(bookId);

        if (selectedBook.isPresent()){
            Book existingBook = selectedBook.get();
            Collection<Author> listAuthor = new ArrayList<>();
            String[] authorNames = authors.split(", ");
            for (String authorName : authorNames) {
                String[] nameParts = authorName.split(" ");
                if (nameParts.length == 2) {
                    Author oldAuthor = authorRepository.findByFirstNameAndLastName(nameParts[0],nameParts[1]);
                    if (oldAuthor == null){
                        Author newAuthor = new Author();
                        newAuthor.setFirstName(nameParts[0]);
                        newAuthor.setLastName(nameParts[1]);
                        listAuthor.add(newAuthor);
                    }
                    else{
                        listAuthor.add(oldAuthor);
                    }
                }
            }

            if (!file.isEmpty()){
                try {
                    existingBook.setImage(Base64.getEncoder().encodeToString(file.getBytes()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            existingBook.setAuthors(listAuthor);
            existingBook.setTitle(book.getTitle());
            existingBook.setPublication_year((book.getPublication_year()));
            existingBook.setCategory(book.getCategory());
            existingBook.setDescription(book.getDescription());

            bookRepository.save(existingBook);
        }

    }


    private BookDto convertEntityToDto(Book book){
        BookDto bookDto = new BookDto();
        bookDto.setCategory(book.getCategory());
        bookDto.setDescription(book.getDescription());
        bookDto.setPublication_year(book.getPublication_year());
        bookDto.setTitle(book.getTitle());
        bookDto.setBook_id(book.getBook_id());
        Collection <AuthorDto> authorsDto = new ArrayList<>();

        for (Author author : book.getAuthors()) {
            AuthorDto authorDto = new AuthorDto();
            authorDto.setFirstName(author.getFirstName());
            authorDto.setLastName(author.getLastName());
            // Set other properties of AuthorDto as needed
            authorsDto.add(authorDto);
        }
        bookDto.setAuthors(authorsDto);
        bookDto.setImage(book.getImage());
        return bookDto;
    }
}