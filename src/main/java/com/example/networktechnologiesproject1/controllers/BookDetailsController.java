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


@RestController
@RequestMapping("/bookDetail")
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
    public BookDetail addBookDetail(@RequestBody BookDetail bookDetail) {
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
    public Iterable<BookDetail> getAllBookDetails() {
        return bookDetailRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDetail> getBookDetailById(@PathVariable Integer id) {
        BookDetail bookDetail = bookDetailRepository.findById(id)
                .orElseThrow(() -> new BookDetailNotFoundException(id));
        return ResponseEntity.ok(bookDetail);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BookDetail> updateBookDetail(@PathVariable Integer id, @RequestBody BookDetail bookDetail) {
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
    public ResponseEntity<Void> deleteBookDetail(@PathVariable Integer id) {
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
