package com.example.networktechnologiesproject1.services;

import com.example.networktechnologiesproject1.entities.Review;
import com.example.networktechnologiesproject1.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }

    public Optional<Review> findById(Integer reviewId) {
        return reviewRepository.findById(reviewId);
    }

    public Iterable<Review> findAllReviews() {
        return reviewRepository.findAll();
    }

    public void deleteReview(Integer reviewId) {
        reviewRepository.deleteById(reviewId);
    }
}
