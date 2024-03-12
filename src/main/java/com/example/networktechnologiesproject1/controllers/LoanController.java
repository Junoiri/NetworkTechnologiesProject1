package com.example.networktechnologiesproject1.controllers;

import com.example.networktechnologiesproject1.entities.Loan;
import com.example.networktechnologiesproject1.repositories.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loan")
public class LoanController {

    private final LoanRepository loanRepository;

    @Autowired
    public LoanController(LoanRepository loanRepository){
        this.loanRepository = loanRepository;
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody Loan addLoan(@RequestBody Loan loan){
        return loanRepository.save(loan);
    }

    @GetMapping("/getAll")
    public @ResponseBody Iterable<Loan> getAllLoans(){
        return loanRepository.findAll();
    }
}
