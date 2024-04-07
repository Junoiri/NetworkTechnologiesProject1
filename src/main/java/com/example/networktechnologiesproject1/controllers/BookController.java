package com.example.networktechnologiesproject1.controllers;

import com.example.networktechnologiesproject1.entities.Book;
import com.example.networktechnologiesproject1.exceptions.BookNotFoundException;
import com.example.networktechnologiesproject1.exceptions.BookValidationException;
import com.example.networktechnologiesproject1.exceptions.DuplicateBookException;
import com.example.networktechnologiesproject1.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for handling book-related operations.
 */
@RestController
@RequestMapping("/book")
public class BookController {

    private final BookRepository bookRepository;

    /**
     * Constructor injecting BookRepository dependency.
     * @param bookRepository The BookRepository instance.
     */
    @Autowired
    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Adds a new book to the repository.
     * @param book The book object to be added.
     * @return The added book object.
     */
    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public Book addBook(@RequestBody Book book) {
        bookRepository.findByIsbn(book.getIsbn()).ifPresent(b -> {
            throw new DuplicateBookException(book.getIsbn());
        });

        validateBook(book);

        return bookRepository.save(book);
    }

    /**
     * Retrieves all books from the repository.
     * @return Iterable containing all books.
     */
    @GetMapping("/getAll")
    public Iterable<Book> getAll() {
        return bookRepository.findAll();
    }

    /**
     * Retrieves a book by its ID.
     * @param id The ID of the book to retrieve.
     * @return ResponseEntity containing the book object if found, or 404 if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Integer id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        return ResponseEntity.ok(book);
    }

    /**
     * Updates details of a book.
     * @param id The ID of the book to update.
     * @param bookDetails The updated details of the book.
     * @return ResponseEntity containing the updated book object.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Integer id, @RequestBody Book bookDetails) {
        return bookRepository.findById(id).map(existingBook -> {
            bookRepository.findByIsbn(bookDetails.getIsbn()).ifPresent(b -> {
                if (!b.getBookId().equals(existingBook.getBookId())) {
                    throw new DuplicateBookException(bookDetails.getIsbn());
                }
            });

            validateBook(bookDetails);

            updateBookDetails(existingBook, bookDetails);

            return new ResponseEntity<>(bookRepository.save(existingBook), HttpStatus.OK);
        }).orElseThrow(() -> new BookNotFoundException(id));
    }


    /**
     * Deletes a book by its ID.
     * @param id The ID of the book to delete.
     * @return ResponseEntity indicating success or failure of deletion.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Integer id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        bookRepository.delete(book);
        return ResponseEntity.noContent().build();
    }

    /**
     * Validates a book object for mandatory fields and format.
     * @param book The book object to validate.
     * @throws BookValidationException If validation fails.
     */
    private void validateBook(Book book) {
        // Check if the title is null or empty
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new BookValidationException("Book title cannot be empty.");
        }
        // Check if the ISBN is null or empty
        if (book.getIsbn() == null || book.getIsbn().trim().isEmpty()) {
            throw new BookValidationException("ISBN cannot be empty.");
        }
        // Validate ISBN format
        if (!book.getIsbn().matches("^\\d{13}$")) {
            throw new BookValidationException("ISBN must be 13 digits long.");
        }
        // Check if the author is null or empty
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new BookValidationException("Author cannot be empty.");
        }
        // Check if the publisher is null or empty
        if (book.getPublisher() == null || book.getPublisher().trim().isEmpty()) {
            throw new BookValidationException("Publisher cannot be empty.");
        }
        // Validate the year
        int currentYear = java.time.Year.now().getValue();
        if (book.getYear() == null || book.getYear() < 1800 || book.getYear() > currentYear) {
            throw new BookValidationException("Year must be between 1800 and the current year.");
        }
        // Validate available copies
        if (book.getAvailableCopies() == null || book.getAvailableCopies() < 0) {
            throw new BookValidationException("Available copies cannot be negative.");
        }
    }

    /**
     * Updates details of an existing book with new details.
     * @param existingBook The existing book object.
     * @param bookDetails The updated details of the book.
     */
    private void updateBookDetails(Book existingBook, Book bookDetails) {
        existingBook.setIsbn(bookDetails.getIsbn());
        existingBook.setTitle(bookDetails.getTitle());
        existingBook.setAuthor(bookDetails.getAuthor());
        existingBook.setPublisher(bookDetails.getPublisher());
        existingBook.setYear(bookDetails.getYear());
        existingBook.setAvailableCopies(bookDetails.getAvailableCopies());
    }
}
