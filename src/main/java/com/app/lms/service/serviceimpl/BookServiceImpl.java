package com.app.lms.service.serviceimpl;

import com.app.lms.entity.*;
import com.app.lms.repository.AuthorRepository;
import com.app.lms.repository.BookRepository;
import com.app.lms.service.BookService;
import com.app.lms.web.AuthorDto;
import com.app.lms.web.BookDto;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository,
                           AuthorRepository authorRepository
                          ) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;

    }

    // Convert BookDto to Book, then save book into database
    @Override
    public void saveBook(BookDto bookDto, MultipartFile file) {
        Book book = new Book();
        book.setTitle(bookDto.getTitle());
        book.setDescription(bookDto.getDescription());
        book.setPublication_year(bookDto.getPublication_year()); // Use correct property name
        book.setCategory(bookDto.getCategory());
        book.setDeleted(false);

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

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

    // Find book based on title
    @Override
    public Book findByBook(String title) {
        return bookRepository.findByTitle(title);
    }

    // Retrieve list of all books
    @Override
    public List<BookDto> findAllBooks() {
        List<Book> books = bookRepository.findAll();
        return books.stream().map((book) -> convertEntityToDto(book))
                .collect(Collectors.toList());
    }

    // Retrieve pages of all books
    @Override
    public Page<BookDto> findPaginated(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<Book> bookPage = this.bookRepository.findAll(pageable); //find pages of Book entity

        List<BookDto> bookDtoList = bookPage.getContent().stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(bookDtoList, pageable, bookPage.getTotalElements());
    }

    // Find book based on Book Id
    @Override
    public Optional<BookDto> findById(long id) {
        Optional<Book> bookOptional = bookRepository.findById(id);
        return bookOptional.map(this::convertEntityToDto);
    }


    // Update existing book
    @Override
    public void updateBooks(BookDto book, Long bookId, MultipartFile file, String authors) {
        Optional<Book> selectedBook = bookRepository.findById(bookId);

        // if exists, update with new book information
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

    // Delete book
    public void deleteBooks (Long id){
        Book bookToDelete = bookRepository.findById(id).orElse(null);

        if (bookToDelete != null) {

            // Delete the member
            bookToDelete.setDeleted(true);
            bookRepository.save(bookToDelete);
        }
    }

    // Search and retrieve pages of book based on search filters
    public Page<BookDto> searchBooks(String query, Pageable pageable, String searchBy, String statusFilter
                                     , String sort, String order) {

        // set status
        BookStatus status = BookStatus.AVAILABLE;
        if (statusFilter.equals("ALL")){
            status = null;
        }
        else if (statusFilter.equals("AVAILABLE")){
            status = BookStatus.AVAILABLE;
        }
        else if (statusFilter.equals("CHECKEDOUT")){
            status = BookStatus.CHECKED_OUT;
        }

        // set sort order
        Sort.Direction direction = Sort.Direction.ASC;

        if (order.equals("desc")){
            direction = Sort.Direction.DESC;
        }

        // set sort option
        Sort sortable = Sort.by(direction, "title"); // Default sorting by title

        if (sort != null) {
            switch (sort) {
                case "publicationYear":
                    sortable = Sort.by(direction, "publication_year");
                    break;
                case "author":
                    sortable = Sort.by(direction, "a.firstName");
                    break;
                case "category":
                    sortable = Sort.by(direction, "category");
                    break;
            }
        }

        Page<Book> searchedBooks = null;
        if (searchBy.equals("any")){
            searchedBooks = bookRepository.searchForBooksWithStatusAndByAny(query, status, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));
        }
        else { // if search filter != "any"
            searchedBooks = bookRepository.searchForBooksWithStatusAndNotAny(query, searchBy, status, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));
        }

        List<BookDto> matchedBooks = searchedBooks.getContent().stream()
                .map(this::convertEntityToDto).collect(Collectors.toList());

        return new PageImpl<>(matchedBooks, pageable, searchedBooks.getTotalElements());
    }

    // Convert Book entity to that of BookDto
    public BookDto convertEntityToDto(Book book){
        BookDto bookDto = new BookDto();
        bookDto.setCategory(book.getCategory());
        bookDto.setDescription(book.getDescription());
        bookDto.setPublication_year(book.getPublication_year());
        bookDto.setTitle(book.getTitle());
        bookDto.setBook_id(book.getBook_id());
        bookDto.setStatus(book.getStatus());
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