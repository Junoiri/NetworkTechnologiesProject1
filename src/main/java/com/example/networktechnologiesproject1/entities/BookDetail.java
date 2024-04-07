package com.example.networktechnologiesproject1.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Schema(description = "Entity for storing additional details about a book, fetched from an external API")
public class BookDetail {

    @Id
    @Schema(description = "Unique identifier for the book detail, references the primary key of the Book entity", example = "1", required = true)
    private Integer bookId;

    @Column(name = "genre")
    @Schema(description = "The genre(s) of the book", example = "Fantasy")
    private String genre;

    @Column(name = "summary", length = 1000)
    @Schema(description = "A short summary or description of the book", example = "An epic tale of adventure...")
    private String summary;

    @Column(name = "cover_image_url")
    @Schema(description = "URL to the book's cover image", example = "https://example.com/book-cover.jpg")
    private String coverImageUrl;

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }
}
