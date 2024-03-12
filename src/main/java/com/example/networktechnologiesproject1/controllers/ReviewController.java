package com.example.networktechnologiesproject1.controllers;

import com.example.networktechnologiesproject1.entities.Review;
import com.example.networktechnologiesproject1.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/review")
public class ReviewController {

    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewController(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody Review addReview(@RequestBody Review review) {
        return reviewRepository.save(review);
    }

    @GetMapping("/getAll")
    public @ResponseBody Iterable<Review> getAllReviews() {
        return reviewRepository.findAll();
    }
}


