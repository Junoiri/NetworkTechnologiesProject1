package com.example.networktechnologiesproject1.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Entity class representing a book.
 */
@Entity
@Schema(description = "Entity representing a book in the library")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(description = "Unique identifier of the book", example = "1", required = true)
    private Integer bookId;

    @Schema(description = "International Standard Book Number", example = "978-3-16-148410-0")
    private String isbn;

    @Schema(description = "Title of the book", example = "The Great Gatsby")
    private String title;

    @Schema(description = "Author(s) of the book", example = "F. Scott Fitzgerald")
    private String author;

    @Schema(description = "Publisher of the book", example = "Charles Scribner's Sons")
    private String publisher;

    @Schema(description = "Year when the book was published", example = "1925")
    private Long year;

    @Schema(description = "Number of available copies of the book", example = "3")
    private Long availableCopies;

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Long getYear() {
        return year;
    }

    public void setYear(Long year) {
        this.year = year;
    }

    public Long getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(Long availableCopies) {
        this.availableCopies = availableCopies;
    }
}
