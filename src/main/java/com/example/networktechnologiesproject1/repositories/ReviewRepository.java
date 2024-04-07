package com.example.networktechnologiesproject1.repositories;

import com.example.networktechnologiesproject1.entities.Review;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends CrudRepository<Review, Integer> {

    /**
     * Checks if a review exists for the given userId and bookId.
     *
     * @param userId the ID of the user
     * @param bookId the ID of the book
     * @return true if a review exists, false otherwise
     */
    boolean existsByUserIdAndBookId(Integer userId, Integer bookId);
}
