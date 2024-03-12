package com.example.networktechnologiesproject1.controllers;

import com.example.networktechnologiesproject1.entities.BookDetail;
import com.example.networktechnologiesproject1.repositories.BookDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookDetail")
public class BookDetailsController {

    private final BookDetailRepository bookDetailRepository;

    @Autowired
    public BookDetailsController(BookDetailRepository bookDetailRepository){
        this.bookDetailRepository = bookDetailRepository;
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody BookDetail addBookDetail(@RequestBody BookDetail bookDetail){
        return bookDetailRepository.save(bookDetail);
    }

    @GetMapping("/getAll")
    public @ResponseBody Iterable<BookDetail> getAllBookDetails(){
        return bookDetailRepository.findAll();
    }
}