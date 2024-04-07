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

@RestController
@RequestMapping("/review")
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
    @ResponseStatus(HttpStatus.CREATED)
    public Review addReview(@RequestBody Review review) {
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
    public Iterable<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable Integer id) {
        Review review = reviewRepository.findById(id).orElseThrow(() -> new ReviewNotFoundException("Review with ID " + id + " not found."));
        return ResponseEntity.ok(review);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Review> updateReview(@PathVariable Integer id, @RequestBody Review reviewDetails) {
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
    public ResponseEntity<Void> deleteReview(@PathVariable Integer id) {
        Review review = reviewRepository.findById(id).orElseThrow(() -> new ReviewNotFoundException("Review with ID " + id + " not found."));
        reviewRepository.delete(review);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
