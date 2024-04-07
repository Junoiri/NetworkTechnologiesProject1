package com.example.networktechnologiesproject1.controllers;

import com.example.networktechnologiesproject1.entities.Review;
import com.example.networktechnologiesproject1.exceptions.*;
import com.example.networktechnologiesproject1.repositories.ReviewRepository;
import com.example.networktechnologiesproject1.repositories.BookRepository; // Assume this exists
import com.example.networktechnologiesproject1.repositories.UserRepository; // Assume this exists
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/review")
@Tag(name = "Review Management", description = "Endpoints for managing user reviews of books. Allows for the creation, retrieval, update, and deletion of book reviews.")
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Autowired
    public ReviewController(ReviewRepository reviewRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @PostMapping("/add")
    @Operation(summary = "Add a review", description = "Creates a new review for a book by a user. Checks for the existence of both user and book, and ensures the review does not duplicate.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Review successfully added", content = @Content(schema = @Schema(implementation = Review.class))),
            @ApiResponse(responseCode = "400", description = "Invalid review details, user or book not found, or review duplicates"),
            @ApiResponse(responseCode = "404", description = "User or book to be reviewed not found")
    })
    public Review addReview(@RequestBody(description = "Review object with details including user ID, book ID, rating, comment, and review date") Review review) {
        if (!userRepository.existsById(review.getUserId())) {
            throw new UserNotFoundException("User with ID " + review.getUserId() + " not found.");
        }
        if (!bookRepository.existsById(review.getBookId())) {
            throw new BookNotFoundException(review.getBookId());
        }
        if (reviewRepository.existsByUserIdAndBookId(review.getUserId(), review.getBookId())) {
            throw new DuplicateReviewException("Duplicate review for book and user not allowed.");
        }
        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new InvalidRatingException("Rating must be between 1 and 5.");
        }
        return reviewRepository.save(review);
    }

    @GetMapping("/getAll")
    @Operation(summary = "Get all reviews", description = "Retrieves a list of all reviews made by users on books, including ratings and comments.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all reviews", content = @Content(schema = @Schema(implementation = Review.class)))
    public Iterable<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a review by ID", description = "Retrieves details of a specific review by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the review", content = @Content(schema = @Schema(implementation = Review.class))),
            @ApiResponse(responseCode = "404", description = "Review not found with the specified ID")
    })
    public ResponseEntity<Review> getReviewById(@PathVariable @Parameter(description = "Unique identifier of the review to retrieve") Integer id) {
        Review review = reviewRepository.findById(id).orElseThrow(() -> new ReviewNotFoundException("Review with ID " + id + " not found."));
        return ResponseEntity.ok(review);
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Update a review", description = "Updates the details of an existing review, including the rating, comment, and review date.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review successfully updated", content = @Content(schema = @Schema(implementation = Review.class))),
            @ApiResponse(responseCode = "400", description = "Invalid review details supplied for update"),
            @ApiResponse(responseCode = "404", description = "Review not found with the specified ID")
    })
    public ResponseEntity<Review> updateReview(@PathVariable @Parameter(description = "Unique identifier of the review to update") Integer id,
                                               @RequestBody(description = "Review object with updated details") Review reviewDetails) {
        return reviewRepository.findById(id)
                .map(existingReview -> {
                    if (reviewDetails.getRating() < 1 || reviewDetails.getRating() > 5) {
                        throw new InvalidRatingException("Rating must be between 1 and 5.");
                    }
                    existingReview.setBookId(reviewDetails.getBookId());
                    existingReview.setUserId(reviewDetails.getUserId());
                    existingReview.setRating(reviewDetails.getRating());
                    existingReview.setComment(reviewDetails.getComment());
                    existingReview.setReviewDate(reviewDetails.getReviewDate());
                    Review updatedReview = reviewRepository.save(existingReview);
                    return new ResponseEntity<>(updatedReview, HttpStatus.OK);
                })
                .orElseThrow(() -> new ReviewNotFoundException("Review with ID " + id + " not found."));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete a review", description = "Removes a review from the system based on its ID. This might be necessary if the review is no longer relevant or was made in error.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Review successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Review not found with the specified ID")
    })
    public ResponseEntity<Void> deleteReview(@PathVariable @Parameter(description = "Unique identifier of the review to delete") Integer id) {
        Review review = reviewRepository.findById(id).orElseThrow(() -> new ReviewNotFoundException("Review with ID " + id + " not found."));
        reviewRepository.delete(review);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
