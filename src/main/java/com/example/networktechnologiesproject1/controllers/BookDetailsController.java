package com.example.networktechnologiesproject1.controllers;

import com.example.networktechnologiesproject1.entities.BookDetail;
import com.example.networktechnologiesproject1.repositories.BookDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookDetail")
public class BookDetailsController {

    private final BookDetailRepository bookDetailRepository;

    @Autowired
    public BookDetailsController(BookDetailRepository bookDetailRepository) {
        this.bookDetailRepository = bookDetailRepository;
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public BookDetail addBookDetail(@RequestBody BookDetail bookDetail) {
        return bookDetailRepository.save(bookDetail);
    }

    @GetMapping("/getAll")
    public Iterable<BookDetail> getAllBookDetails() {
        return bookDetailRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDetail> getBookDetailById(@PathVariable Integer id) {
        return bookDetailRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BookDetail> updateBookDetail(@PathVariable Integer id, @RequestBody BookDetail bookDetail) {
        return bookDetailRepository.findById(id)
                .map(existingDetail -> {
                    existingDetail.setGenre(bookDetail.getGenre());
                    existingDetail.setSummary(bookDetail.getSummary());
                    existingDetail.setCoverImageUrl(bookDetail.getCoverImageUrl());
                    BookDetail updatedDetail = bookDetailRepository.save(existingDetail);
                    return new ResponseEntity<>(updatedDetail, HttpStatus.OK);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteBookDetail(@PathVariable Integer id) {
        return bookDetailRepository.findById(id)
                .map(detail -> {
                    bookDetailRepository.delete(detail);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
