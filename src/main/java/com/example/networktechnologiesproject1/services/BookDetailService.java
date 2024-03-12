package com.example.networktechnologiesproject1.services;

import com.example.networktechnologiesproject1.entities.BookDetail;
import com.example.networktechnologiesproject1.repositories.BookDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class BookDetailService {

    private final BookDetailRepository bookDetailRepository;

    @Autowired
    public BookDetailService(BookDetailRepository bookDetailRepository) {
        this.bookDetailRepository = bookDetailRepository;
    }

    public BookDetail saveBookDetail(BookDetail bookDetail) {
        return bookDetailRepository.save(bookDetail);
    }

    public Optional<BookDetail> findById(Integer bookId) {
        return bookDetailRepository.findById(bookId);
    }

    public Iterable<BookDetail> findAllBookDetails() {
        return bookDetailRepository.findAll();
    }

    public void deleteBookDetail(Integer bookId) {
        bookDetailRepository.deleteById(bookId);
    }
}
