package com.example.networktechnologiesproject1.repositories;

import com.example.networktechnologiesproject1.entities.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends CrudRepository<Book, Integer> {
}
