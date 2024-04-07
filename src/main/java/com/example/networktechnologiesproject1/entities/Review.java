package com.example.networktechnologiesproject1.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Date;

@Entity
@Schema(description = "Entity representing a user's review of a book")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(description = "Unique identifier for each review", example = "102", required = true)
    private Integer reviewId;

    @Schema(description = "Identifier for the book being reviewed, references the Book entity", example = "1")
    private Integer bookId;

    @Schema(description = "Identifier for the user who wrote the review, references the User entity", example = "42")
    private Integer userId;

    @Schema(description = "The rating given by the user", example = "4.5")
    private Double rating;

    @Schema(description = "The textual review left by the user", example = "An enthralling journey from start to finish.")
    private String comment;

    @Schema(description = "The date the review was posted", example = "2023-03-10T14:30:00Z")
    private Date reviewDate;
    public Integer getReviewId() {
        return reviewId;
    }

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }
}
