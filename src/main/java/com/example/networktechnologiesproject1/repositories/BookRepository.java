package com.example.networktechnologiesproject1.repositories;

import com.example.networktechnologiesproject1.entities.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on Book entities.
 */
@Repository
public interface BookRepository extends CrudRepository<Book, Integer> {
    Optional<Book> findByIsbn(String isbn);

}
