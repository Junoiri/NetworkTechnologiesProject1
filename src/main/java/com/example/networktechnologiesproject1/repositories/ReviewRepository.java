package com.example.networktechnologiesproject1.repositories;

import com.example.networktechnologiesproject1.entities.Review;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends CrudRepository<Review, Integer> {
}
