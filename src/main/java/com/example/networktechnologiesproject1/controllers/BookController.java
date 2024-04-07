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

@RestController
@RequestMapping("/book")
public class BookController {

    private final BookRepository bookRepository;

    @Autowired
    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public Book addBook(@RequestBody Book book) {
        bookRepository.findByIsbn(book.getIsbn()).ifPresent(b -> {
            throw new DuplicateBookException(book.getIsbn());
        });

        validateBook(book);

        return bookRepository.save(book);
    }

    @GetMapping("/getAll")
    public Iterable<Book> getAll() {
        return bookRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Integer id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        return ResponseEntity.ok(book);
    }

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


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Integer id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        bookRepository.delete(book);
        return ResponseEntity.noContent().build();
    }

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

    private void updateBookDetails(Book existingBook, Book bookDetails) {
        existingBook.setIsbn(bookDetails.getIsbn());
        existingBook.setTitle(bookDetails.getTitle());
        existingBook.setAuthor(bookDetails.getAuthor());
        existingBook.setPublisher(bookDetails.getPublisher());
        existingBook.setYear(bookDetails.getYear());
        existingBook.setAvailableCopies(bookDetails.getAvailableCopies());
    }
}
