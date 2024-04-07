package com.example.networktechnologiesproject1.controllers;

import com.example.networktechnologiesproject1.entities.BookDetail;
import com.example.networktechnologiesproject1.exceptions.*;
import com.example.networktechnologiesproject1.repositories.BookDetailRepository;
import com.example.networktechnologiesproject1.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.networktechnologiesproject1.repositories.BookRepository;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;



/**
 * Controller for managing book details, which include genre, summary, and cover image URL.
 */
@RestController
@RequestMapping("/bookDetail")
@Tag(name = "Book Details", description = "Endpoints for managing detailed information about books such as genre, summary, and cover image, which are additional to the basic book information.")
public class BookDetailsController {

    private final BookDetailRepository bookDetailRepository;
    private final BookRepository bookRepository;

    @Autowired
    public BookDetailsController(BookDetailRepository bookDetailRepository, BookRepository bookRepository) {
        this.bookDetailRepository = bookDetailRepository;
        this.bookRepository = bookRepository;
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add book detail", description = "Creates a new book detail record with genre, summary, and cover image URL. Validates the detail's data and checks the associated book exists.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Book detail successfully created", content = @Content(schema = @Schema(implementation = BookDetail.class))),
            @ApiResponse(responseCode = "400", description = "Invalid book detail supplied or associated book does not exist"),
            @ApiResponse(responseCode = "409", description = "Duplicate book detail for the same book and genre")
    })
    public BookDetail addBookDetail(@RequestBody(description = "BookDetail object to be added, containing genre, summary, and cover image URL") BookDetail bookDetail) {
        validateBookDetail(bookDetail);
        if (!checkAssociatedBookExists(bookDetail.getBookId())) {
            throw new AssociatedBookNotFoundException(bookDetail.getBookId());
        }
        if (bookDetailRepository.existsByBookIdAndGenre(bookDetail.getBookId(), bookDetail.getGenre())) {
            throw new BookDetailDuplicateException("Book ID and Genre");
        }
        return bookDetailRepository.save(bookDetail);
    }


    @GetMapping("/getAll")
    @Operation(summary = "Get all book details", description = "Retrieves all book detail records from the database, including genres, summaries, and cover image URLs.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all book details", content = @Content(schema = @Schema(implementation = BookDetail.class)))
    public Iterable<BookDetail> getAllBookDetails() {
        return bookDetailRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get book detail by ID", description = "Retrieves a specific book detail record using its ID. The details include genre, summary, and cover image URL.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved book detail", content = @Content(schema = @Schema(implementation = BookDetail.class))),
            @ApiResponse(responseCode = "404", description = "Book detail not found with the provided ID")
    })
    public ResponseEntity<BookDetail> getBookDetailById(@PathVariable @Parameter(description = "Unique identifier of the book detail to retrieve") Integer id) {
        BookDetail bookDetail = bookDetailRepository.findById(id)
                .orElseThrow(() -> new BookDetailNotFoundException(id));
        return ResponseEntity.ok(bookDetail);
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Update book detail", description = "Updates an existing book detail record. Validates the new detail data and checks for duplicates before updating.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated book detail", content = @Content(schema = @Schema(implementation = BookDetail.class))),
            @ApiResponse(responseCode = "400", description = "Invalid book detail supplied for update"),
            @ApiResponse(responseCode = "404", description = "Book detail not found with the provided ID"),
            @ApiResponse(responseCode = "409", description = "Duplicate book detail for the same book and genre")
    })
    public ResponseEntity<BookDetail> updateBookDetail(@PathVariable @Parameter(description = "Unique identifier of the book detail to update") Integer id,
                                                       @RequestBody(description = "Updated BookDetail object") BookDetail bookDetail) {
        return bookDetailRepository.findById(id).map(existingDetail -> {
            validateBookDetail(bookDetail);
            if (!existingDetail.getGenre().equals(bookDetail.getGenre()) &&
                    bookDetailRepository.existsByBookIdAndGenre(bookDetail.getBookId(), bookDetail.getGenre())) {
                throw new BookDetailDuplicateException("Book ID and Genre");
            }
            updateBookDetails(existingDetail, bookDetail);
            return new ResponseEntity<>(bookDetailRepository.save(existingDetail), HttpStatus.OK);
        }).orElseThrow(() -> new BookDetailNotFoundException(id));
    }


    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete book detail", description = "Deletes a book detail record by ID. Checks for any dependencies that might prevent deletion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted book detail"),
            @ApiResponse(responseCode = "400", description = "Unauthorized attempt to delete a book detail or dependencies exist"),
            @ApiResponse(responseCode = "404", description = "Book detail not found with the provided ID")
    })
    public ResponseEntity<Void> deleteBookDetail(@PathVariable @Parameter(description = "Unique identifier of the book detail to delete") Integer id) {
        BookDetail bookDetail = bookDetailRepository.findById(id)
                .orElseThrow(() -> new BookDetailNotFoundException(id));
        if (!canDeleteBookDetail(bookDetail)) {
        throw new UnauthorizedDetailChangeException();
        }
        bookDetailRepository.delete(bookDetail);
        return ResponseEntity.noContent().build();
    }

    private void validateBookDetail(BookDetail bookDetail) {
        if (bookDetail.getGenre() == null || bookDetail.getGenre().trim().isEmpty()) {
            throw new BookDetailValidationException("Genre cannot be empty.");
        } else if (!isGenreSupported(bookDetail.getGenre())) {
            throw new GenreNotSupportedException(bookDetail.getGenre());
        }
        if (bookDetail.getCoverImageUrl() == null || bookDetail.getCoverImageUrl().trim().isEmpty()) {
            throw new BookDetailValidationException("Cover image URL cannot be empty.");
        } else if (!isValidCoverImageUrl(bookDetail.getCoverImageUrl())) {
            throw new InvalidCoverImageUrlException(bookDetail.getCoverImageUrl());
        }
        if (bookDetail.getSummary() == null || bookDetail.getSummary().trim().isEmpty()) {
            throw new BookDetailValidationException("Summary cannot be empty.");
        } else if (bookDetail.getSummary().length() > 1000) {
            throw new BookDetailValidationException("Summary exceeds maximum length of 1000 characters.");
        }
        else {
            throw new BookDetailValidationException("An unknown error regarding book details occurred.");
        }
    }


    private void updateBookDetails(BookDetail existingDetail, BookDetail bookDetail) {
        existingDetail.setGenre(bookDetail.getGenre());
        existingDetail.setSummary(bookDetail.getSummary());
        existingDetail.setCoverImageUrl(bookDetail.getCoverImageUrl());
    }

    private boolean checkAssociatedBookExists(Integer bookId) {
        return bookRepository.existsById(bookId);
    }

    private boolean isGenreSupported(String genre) {
        List<String> supportedGenres = Arrays.asList("Fiction", "Non-Fiction", "Science Fiction", "Biography", "History", "Children", "Fantasy", "Other");
        return supportedGenres.contains(genre);
    }

    private boolean isValidCoverImageUrl(String url) {
        try {
            URL verifiedUrl = new URL(url);
            verifiedUrl.toURI(); // Checks if the URL is a valid

            // HEAD request to check if the resource is reachable
            HttpURLConnection httpURLConnection = (HttpURLConnection) verifiedUrl.openConnection();
            httpURLConnection.setRequestMethod("HEAD");
            httpURLConnection.setConnectTimeout(5000); // Timeout
            httpURLConnection.setReadTimeout(5000);

            // Check the response code
            int responseCode = httpURLConnection.getResponseCode();
            return (200 <= responseCode && responseCode <= 399);

        } catch (IOException | URISyntaxException e) {
            return false;
        }
    }

    // Placeholders for additional methods
    private boolean canDeleteBookDetail(BookDetail bookDetail) {
        boolean hasDependencies = checkForDependencies(bookDetail.getBookId());
        return !hasDependencies;
    }

    private boolean checkForDependencies(Integer bookId) {
        return false;
    }
}
