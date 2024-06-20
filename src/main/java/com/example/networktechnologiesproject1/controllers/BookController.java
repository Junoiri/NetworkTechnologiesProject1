package com.example.networktechnologiesproject1.controllers;

import com.example.networktechnologiesproject1.entities.Book;
import com.example.networktechnologiesproject1.exceptions.BookNotFoundException;
import com.example.networktechnologiesproject1.exceptions.BookValidationException;
import com.example.networktechnologiesproject1.exceptions.DuplicateBookException;
import com.example.networktechnologiesproject1.repositories.BookRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


/**
 * Controller class for handling book-related operations.
 */

@RestController
@RequestMapping("/book")
@Tag(name = "Book Management", description = "APIs for managing books in the library. Includes functionalities for adding, retrieving, updating, and deleting book records, ensuring data integrity and compliance with library policies.")
public class BookController {

    private final BookRepository bookRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

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
@Operation(summary = "Add a new book", description = "Adds a new book to the library's collection. Validates the book's data against predefined criteria to ensure it meets library standards before adding it to the database.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Book created",
                content = @Content(schema = @Schema(implementation = Book.class))),
        @ApiResponse(responseCode = "400", description = "Invalid book details supplied"),
        @ApiResponse(responseCode = "409", description = "Duplicate book ISBN provided")
})
public Book addBook(@RequestBody @Parameter(description = "The book object containing detailed information about the book to be added. This includes the ISBN, title, author, publisher, year of publication, and the number of available copies.") Book book) {
    logger.info("Attempting to add book: {}", book.toString());

    bookRepository.findByIsbn(book.getIsbn()).ifPresent(b -> {
        logger.error("Duplicate ISBN: {}", book.getIsbn());
        throw new DuplicateBookException(book.getIsbn());
    });

    validateBook(book);

    Book savedBook = bookRepository.save(book);
    logger.info("Book with ISBN: {} added successfully", savedBook.getIsbn());
    return savedBook;
}

    /**
     * Retrieves all books from the repository.
     * @return Iterable containing all books.
     */
    @GetMapping("/getAll")
    @Operation(summary = "Retrieve all books", description = "Fetches a comprehensive list of all books currently available in the library's collection. This includes detailed information about each book.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of books", content = @Content(schema = @Schema(implementation = Book.class)))
    public Iterable<Book> getAll() {
        return bookRepository.findAll();
    }

    /**
     * Retrieves a book by its ID.
     * @param id The ID of the book to retrieve.
     * @return ResponseEntity containing the book object if found, or 404 if not found.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Retrieve a book by ID", description = "Fetches detailed information about a specific book identified by its unique ID in the library's database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found and retrieved the book information", content = @Content(schema = @Schema(implementation = Book.class))),
            @ApiResponse(responseCode = "404", description = "The book with the specified ID was not found in the database.")
    })
    public ResponseEntity<Book> getBookById(@PathVariable @Parameter(description = "The unique identifier of the book to retrieve. This corresponds to the book's ID in the database.") Integer id) {
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
    @Operation(summary = "Update a book's details", description = "Updates the details of an existing book in the library's collection. This can include changes to the title, author, publisher, year of publication, and the number of available copies.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book details successfully updated.", content = @Content(schema = @Schema(implementation = Book.class))),
            @ApiResponse(responseCode = "400", description = "Invalid update details supplied. This can occur if the provided information does not meet the validation criteria."),
            @ApiResponse(responseCode = "404", description = "The book to update was not found in the database."),
            @ApiResponse(responseCode = "409", description = "Conflict occurred due to duplicate ISBN with another book.")
    })
    public ResponseEntity<Book> updateBook(@PathVariable @Parameter(description = "The unique identifier of the book to update.") Integer id, @RequestBody @Parameter(description = "An object containing the updated book details.") Book bookDetails) {
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
    @Operation(summary = "Delete a book from the library", description = "Removes a book from the library's collection based on its unique ID. This action is irreversible.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Book successfully deleted from the library's collection."),
            @ApiResponse(responseCode = "404", description = "The book to delete was not found in the database.")
    })
    public ResponseEntity<Void> deleteBook(@PathVariable @Parameter(description = "The unique identifier of the book to delete.") Integer id) {
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
