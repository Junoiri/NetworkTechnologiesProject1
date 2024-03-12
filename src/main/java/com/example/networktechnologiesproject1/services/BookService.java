package com.example.networktechnologiesproject1.services;

import com.example.networktechnologiesproject1.entities.Book;
import com.example.networktechnologiesproject1.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public Optional<Book> findById(Integer bookId) {
        return bookRepository.findById(bookId);
    }

    public Iterable<Book> findAllBooks() {
        return bookRepository.findAll();
    }

    public void deleteBook(Integer bookId) {
        bookRepository.deleteById(bookId);
    }
}
