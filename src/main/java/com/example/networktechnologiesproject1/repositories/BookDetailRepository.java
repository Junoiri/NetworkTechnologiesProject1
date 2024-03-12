package com.example.networktechnologiesproject1.repositories;

import com.example.networktechnologiesproject1.entities.BookDetail;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookDetailRepository extends CrudRepository<BookDetail, Integer> {
}
